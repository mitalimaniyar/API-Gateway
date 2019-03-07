package org.jeavio.apigateway.service;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.jeavio.apigateway.model.GatewayContext;
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class RequestObjectService {

	@Autowired
	SwaggerService swaggerService;

	@Autowired
	DualHashBidiMap cognitoIdMap;

	@Autowired
	EventCartridge eventCartridge;

	public static Logger log = LoggerFactory.getLogger(RequestObjectService.class);

	public Input getInputObject(String uri, String method, Map<String, String> allParams,
			String requestBody) {

		Input inputRequest = new Input();
		if (requestBody != null && !requestBody.isEmpty()) {
			inputRequest.putBody(requestBody);
		}

		UriTemplate temp = null;
		try {
			temp = swaggerService.getUriTemplate(uri, method);
			inputRequest.putAll(temp.match(uri));
		} catch (Exception e) {
			log.error("Exception occured in finding uri {} : {}", method, uri);
			log.error("Error: ", e);
			e.printStackTrace();
		}

		
		inputRequest.putAll(allParams);

		log.debug("{} : {}  $input object for template :  {}", method, uri, inputRequest);

		return inputRequest;
	}

	public String getRequestBody(HttpServletRequest request, Input inputRequest, String requestBody) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		log.debug("{} : {}  Generating RequestBody", method, uri);

		VelocityEngine velocityEngine = new VelocityEngine();

		GatewayIntegration integrationObject = swaggerService.getGatewayIntegration(uri, method);

//		Template requestTemplate;
		if (integrationObject.getRequestTemplates() != null
				&& integrationObject.getRequestTemplates().get("application/json") != null) {
			if (integrationObject.getRequestTemplates().get("application/json").equals("__passthrough__")) {

				log.debug("{} : {}   \"__passthrough__\" found for sending request body", method, uri);
				log.debug("{} : {} Sending requestBody :  {}", uri, method, requestBody);
				return requestBody;
			} else {

				VelocityContext context = new VelocityContext();

				context.put("context", getContextObject(request));
				context.put("input", inputRequest);
				
				StringWriter writer = new StringWriter();
				String template = integrationObject.getRequestTemplates().get("application/json");

				eventCartridge.attachToContext(context);

				if (velocityEngine.evaluate(context, writer, "requestTemplate", template)) {

					log.debug("{} : {}  Template found for request body and successfully merged ", uri, method);
					log.debug("{} : {} RequestBody  :  {}", uri, method, writer.toString());

					return writer.toString();
				} else {

					log.debug("{} : {}  Template found and merge failed for requestbody..returning null", uri, method);
					return null;
				}
			}

		} else
			return null;
	}

	public String getCognitoId(HttpServletRequest request) {
		String sessionToken = request.getHeader("x-amz-security-token");
		String cognitoId = null;
		if (sessionToken != null && cognitoIdMap.containsKey(sessionToken)) {

			cognitoId = (String) cognitoIdMap.get(sessionToken);
		}

		log.debug("{} : {}  CogId : {} sessionToken :  {} ", request.getMethod(), request.getRequestURI(), cognitoId,
				sessionToken);
		return cognitoId;
	}

	private GatewayContext getContextObject(HttpServletRequest request) {
		GatewayContext context = new GatewayContext();
		
		Map<String, String> identity = new LinkedHashMap<String, String>();
		identity.put("cognitoIdentityId", getCognitoId(request));
		identity.put("userAgent",request.getHeader("User-Agent"));
		
		context.setIdentity(identity);
		context.setHttpMethod(request.getMethod());
		context.setProtocol(request.getProtocol());

		return context;
	}

	private String interpretParamValue(HttpServletRequest request, String value, Input inputRequest) {
		String paramValue = null;
		if (value.indexOf("'") != -1) {
			paramValue = value.replace("'", "");
		} else if (value.equals("context.identity.cognitoIdentityId")) {
			paramValue = getCognitoId(request);
		} else {
			String paramName = value.substring(value.lastIndexOf(".") + 1);
			paramValue = (String) inputRequest.params().get(paramName);
		}
		return paramValue;

	}

	private URI getTargetUri(GatewayIntegration integrationObject, Map<String, String> pathParams,
			Map<String, String> querystringParams) {

		URI targetUri = null;
		UriTemplate targetUrl = new UriTemplate(integrationObject.getUri());
		if (targetUrl.getVariableNames().isEmpty()) {
			try {
				targetUri = new URI(targetUrl.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			targetUri = targetUrl.expand(pathParams);
		}

//		Add querystrin if exist
		if (!querystringParams.isEmpty()) {

			List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : querystringParams.entrySet()) {
				if (entry.getValue() != null)
					queryParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			URIBuilder builder = new URIBuilder().setScheme(targetUri.getScheme()).setHost(targetUri.getHost())
					.setPath(targetUri.getPath()).setParameters(queryParams);
			URI uri = null;
			try {
				uri = builder.build();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;
		}

		return targetUri;
	}

	private HttpUriRequest getRequiredRequest(URI targetUri, String httpMethod, String parsedRequestBody) {
		StringEntity entity = null;
		switch (httpMethod) {

		case "GET":
			HttpGet httpGet = new HttpGet(targetUri);
			return httpGet;

		case "POST":
//		 		HttpPost httpPost=new HttpPost(targetUri);
			HttpPost httpPost = new HttpPost(targetUri);
			try {
				entity = new StringEntity(parsedRequestBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPost.setEntity(entity);
			return httpPost;

		case "PUT":
			HttpPut httpPut = new HttpPut(targetUri);
			try {
				entity = new StringEntity(parsedRequestBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPut.setEntity(entity);
			return httpPut;

		case "PATCH":
			HttpPatch httpPatch = new HttpPatch(targetUri);
			try {
				entity = new StringEntity(parsedRequestBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPatch.setEntity(entity);
			return httpPatch;

		case "DELETE":
			HttpDelete httpDelete = new HttpDelete(targetUri);
			return httpDelete;
		default:
			HttpGet httpGet2 = new HttpGet(targetUri);
			return httpGet2;
		}
	}

	public HttpUriRequest createRequest(HttpServletRequest request, Input inputRequest, String requestBody) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		log.debug("{} : {}  Creating Request for Backend Started", method, uri);

		GatewayIntegration integrationObject = swaggerService.getGatewayIntegration(uri, method);

//		Setting Parameters
		Map<String, String> requestParameters = integrationObject.getRequestParameters();

		Map<String, String> headerParams = new LinkedHashMap<String, String>();
		Map<String, String> pathParams = new LinkedHashMap<String, String>();
		Map<String, String> querystringParams = new LinkedHashMap<String, String>();

		if (requestParameters != null && !requestParameters.isEmpty()) {
			for (String headerName : requestParameters.keySet()) {
				String[] paramGroup = headerName.split("\\.");
				String value = requestParameters.get(headerName);

				String paramValue = interpretParamValue(request, value, inputRequest);

				switch (paramGroup[2]) {
				case "header":
					headerParams.put(paramGroup[3], paramValue);
					break;
				case "path":
					pathParams.put(paramGroup[3], paramValue);
					break;
				case "querystring":
					querystringParams.put(paramGroup[3], paramValue);
					break;
				}
			}
		}
		log.debug("{} : {}  Request Header Params :  {}", method, uri, headerParams);
		log.debug("{} : {}  Request Path Params :  {}", method, uri, pathParams);
		log.debug("{} : {}  Request QueryString Params :  {}", method, uri, querystringParams);

//		Creating Request Body
		String parsedRequestBody = getRequestBody(request, inputRequest, requestBody);

//		Creating Url including Queryparams
		URI targetUri = getTargetUri(integrationObject, pathParams, querystringParams);
		log.debug("{} : {}  Backend URI :  {}  Backend Method : {}", method, uri, targetUri,
				integrationObject.getHttpMethod());

//		Creating Request
		HttpUriRequest targetRequest = getRequiredRequest(targetUri, integrationObject.getHttpMethod(),
				parsedRequestBody);
		if (!headerParams.isEmpty()) {
			for (String param : headerParams.keySet()) {
				targetRequest.addHeader(param, headerParams.get(param));
			}
		}

		targetRequest.setHeader("Accept", "application/json");
		targetRequest.setHeader("Content-type", "application/json");
		if (request.getHeader("referer") != null)
			targetRequest.setHeader("referer", request.getHeader("referer"));

		return targetRequest;
	}

}
