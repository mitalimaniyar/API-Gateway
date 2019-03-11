package org.jeavio.apigateway.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.jeavio.apigateway.model.GatewayContext;
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class RequestHandler {

	@Autowired
	SwaggerService swaggerService;

	@Autowired
	VelocityTemplateHandler velocityTemplateHandler;

	@Autowired
	CognitoCacheService cognitoCacheService;

	public static Logger log = LoggerFactory.getLogger(RequestHandler.class);

	public HttpUriRequest createRequest(HttpServletRequest request, Map<String, String> allParams, String requestBody) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		GatewayIntegration integrationObject = swaggerService.getGatewayIntegration(uri, method);
		Map<String, String> requestParameters = integrationObject.getRequestParameters();

//		Creating Input Object from request parameters & body
		Input inputRequest = getInput(uri, method, allParams, requestBody);

//		Getting Backend Parameters

		Map<String, String> headerParams = new LinkedHashMap<String, String>();
		Map<String, String> pathParams = new LinkedHashMap<String, String>();
		List<NameValuePair> querystringParams = new ArrayList<NameValuePair>();

		parseRequestParams(request, inputRequest, requestParameters, headerParams, pathParams, querystringParams);

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

//		Setting Headers
		setHeaders(request, targetRequest, headerParams);

		return targetRequest;

	}

	public Input getInput(String uri, String method, Map<String, String> allParams, String requestBody) {

		Input inputRequest = new Input();

		if (requestBody != null && !requestBody.isEmpty()) {
			inputRequest.putBody(requestBody);
		}

		UriTemplate template = swaggerService.getUriTemplate(uri, method);

		inputRequest.putAll(template.match(uri));
		inputRequest.putAll(allParams);

		log.debug("{} : {}  $input object for template :  {}", method, uri, inputRequest);

		return inputRequest;
	}

	public String getRequestBody(HttpServletRequest request, Input inputRequest, String requestBody) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		log.debug("{} : {}  Generating RequestBody", method, uri);

		GatewayIntegration integrationObject = swaggerService.getGatewayIntegration(uri, method);

//		Template requestTemplate;
		if (integrationObject.getRequestTemplates() != null
				&& integrationObject.getRequestTemplates().get("application/json") != null) {

			String template = integrationObject.getRequestTemplates().get("application/json");

			if (template.equals("__passthrough__")) {

				log.debug("{} : {}   \"__passthrough__\" found for sending request body", method, uri);
				log.debug("{} : {} Sending requestBody :  {}", uri, method, requestBody);

				return requestBody;

			} else {

				GatewayContext contextRequest = getGatewayContext(request);

				String body = velocityTemplateHandler.processTemplate(uri, method, template, inputRequest,
						contextRequest);

				return body;

			}

		} else
			return null;
	}

	private GatewayContext getGatewayContext(HttpServletRequest request) {
		GatewayContext context = new GatewayContext();

		Map<String, String> identity = new LinkedHashMap<String, String>();
		identity.put("cognitoIdentityId", cognitoCacheService.getCognitoId(request));
		identity.put("userAgent", request.getHeader("User-Agent"));

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
			paramValue = cognitoCacheService.getCognitoId(request);
		} else {
			String paramName = value.substring(value.lastIndexOf(".") + 1);
			paramValue = (String) inputRequest.params().get(paramName);
		}
		return paramValue;

	}

	private URI getTargetUri(GatewayIntegration integrationObject, Map<String, String> pathParams,
			List<NameValuePair> querystringParams) {

		URI targetUri = null;
		UriTemplate backendUri = new UriTemplate(integrationObject.getUri());
		if (backendUri.getVariableNames().isEmpty()) {
			try {
				targetUri = new URI(backendUri.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			targetUri = backendUri.expand(pathParams);
		}

//		Add querystrin if exist
		if (!querystringParams.isEmpty()) {

			URIBuilder builder = new URIBuilder().setScheme(targetUri.getScheme()).setHost(targetUri.getHost()).setPort(targetUri.getPort())
					.setPath(targetUri.getPath()).setParameters(querystringParams);
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

	private void setHeaders(HttpServletRequest request, HttpUriRequest targetRequest,
			Map<String, String> headerParams) {

		if (!headerParams.isEmpty()) {
			for (String param : headerParams.keySet()) {
				targetRequest.addHeader(param, headerParams.get(param));
			}
		}

		targetRequest.setHeader("Accept", "application/json");
		targetRequest.setHeader("Content-type", "application/json");
		targetRequest.setHeader("referer", request.getHeader("referer"));

	}

	private void parseRequestParams(HttpServletRequest request, Input inputRequest,
			Map<String, String> requestParameters, Map<String, String> headerParams, Map<String, String> pathParams,
			List<NameValuePair> querystringParams) {

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
					querystringParams.add(new BasicNameValuePair(paramGroup[3], paramValue));
					break;
				}
			}
		}
	}
}
