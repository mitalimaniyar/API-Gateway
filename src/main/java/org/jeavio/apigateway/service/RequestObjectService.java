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
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.RequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RequestObjectService {

	@Autowired
	URLMethodService urlMethodService;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	Map<String, String> cognitoIdMap;

	ObjectMapper objectMapper = new ObjectMapper();

	public RequestResponse getInputObject(String uri,String method,Map<String, String> allParams, String requestBody) {
		
		RequestResponse inputRequest = new RequestResponse();
		if (requestBody != null && !requestBody.isEmpty()) {
//			try {
//				inputRequest = objectMapper.readValue(requestBody, RequestResponse.class);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			inputRequest.putBody(requestBody);
		}

		UriTemplate temp = null;
		try {
			temp = urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inputRequest.putAll(temp.match(uri));
		inputRequest.putAll(allParams);

		return inputRequest;
	}

	public String getRequestBody(HttpServletRequest request,RequestResponse inputRequest, String requestBody) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		
		VelocityEngine velocityEngine = new VelocityEngine();
		
		GatewayIntegration integrationObject = integrationService.getIntegrationObject(uri, method);

//		Template requestTemplate;
		if (integrationObject.getRequestTemplates() != null
				&& integrationObject.getRequestTemplates().get("application/json") != null) {
			if (integrationObject.getRequestTemplates().get("application/json").equals("__passthrough__")) {
				return requestBody;
			} else {

				VelocityContext context = new VelocityContext();
				if (request.getHeader("x-amz-security-token") != null) {
					context.put("context", getContextObject(request));
				}

				context.put("input", inputRequest);
				StringWriter writer = new StringWriter();
				String template = integrationObject.getRequestTemplates().get("application/json");
				
				if (velocityEngine.evaluate(context, writer, "requestTemplate", template))
					return writer.toString();
				else
					return null;
			}

		} else
			return null;
	}

	public String getCognitoId(HttpServletRequest request) {
		String sessionToken = request.getHeader("x-amz-security-token");
		String cognitoId = null;
		if (sessionToken != null && cognitoIdMap.containsKey(sessionToken)) {
			cognitoId = cognitoIdMap.get(sessionToken);
		}
		return cognitoId;
	}

	private Map<String, Map<String, String>> getContextObject(HttpServletRequest request) {
		Map<String, Map<String, String>> context1 = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> identity = new LinkedHashMap<String, String>();
		identity.put("cognitoIdentityId", getCognitoId(request));
		context1.put("identity", identity);

		return context1;
	}

	public HttpUriRequest createRequest(HttpServletRequest request,RequestResponse inputRequest, String requestBody) {
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		GatewayIntegration integrationObject = integrationService.getIntegrationObject(uri, method);

//		Setting Parameters
		Map<String, String> requestParameters = integrationObject.getRequestParameters();

		Map<String, String> headerParams = new LinkedHashMap<String, String>();
		Map<String, String> pathParams = new LinkedHashMap<String, String>();
		Map<String, String> querystringParams = new LinkedHashMap<String, String>();

		if (requestParameters != null && !requestParameters.isEmpty()) {
			for (String headerName : requestParameters.keySet()) {
				String[] paramGroup = headerName.split("\\.");
				String value = requestParameters.get(headerName);

				String paramValue = interpretParamValue(request,value, inputRequest);

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

//		Creating Request Body
		String parsedRequestBody = getRequestBody(request,inputRequest, requestBody);

//		Creating Url including Queryparams
		URI targetUri = getTargetUri(integrationObject, pathParams, querystringParams);

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

	private String interpretParamValue(HttpServletRequest request,String value, RequestResponse inputRequest) {
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

}
