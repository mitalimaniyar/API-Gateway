package org.jeavio.apigateway.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.jeavio.apigateway.model.CustomHttpRequest;
import org.jeavio.apigateway.model.GatewayContext;
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RequestHandler {

	@Autowired
	SwaggerService swaggerService;

	@Autowired
	VelocityTemplateHandler velocityTemplateHandler;

	@Autowired
	CognitoCacheService cognitoCacheService;

	public HttpUriRequest createRequest(CustomHttpRequest request) {

		
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		GatewayIntegration integrationObject = swaggerService.getGatewayIntegration(uri, method);
		Map<String, String> requestParameters = integrationObject.getRequestParameters();

//		Creating Input Object from request parameters & body
		Input inputRequest = getInput(uri,method,request);

//		Getting Backend Parameters

		Map<String, String> headerParams = new LinkedHashMap<String, String>();
		Map<String, String> pathParams = new LinkedHashMap<String, String>();
		List<NameValuePair> querystringParams = new ArrayList<NameValuePair>();

//		Populating header,querystring,path params' map
		parseRequestParams(request, inputRequest, requestParameters, headerParams, pathParams, querystringParams);

		log.debug("{} : {}  Request Header Params :  {}", method, uri, headerParams);
		log.debug("{} : {}  Request Path Params :  {}", method, uri, pathParams);
		log.debug("{} : {}  Request QueryString Params :  {}", method, uri, querystringParams);

//		Creating Request Body
		String parsedRequestBody = getRequestBody(request, inputRequest);

//		Creating Url including Queryparams
		URI backendUrl = buildRequestUrl(integrationObject, pathParams, querystringParams);

		log.debug("{} : {}  Backend URL :  {}  Backend Method : {}", method, uri, backendUrl,
				integrationObject.getHttpMethod());

//		Creating Request
		HttpUriRequest backendRequest = getBackendRequest(backendUrl, integrationObject.getHttpMethod(),
				parsedRequestBody);

//		Setting Headers
		setHeaders(request, backendRequest, headerParams);

		return backendRequest;

	}

	/*
	 * Creating Input object from requestPayload
	 */
	public Input getInput(String uri, String method, CustomHttpRequest request) {

		Input inputRequest = new Input();

		String requestBody=request.getRequestBody();
		
		if (requestBody != null && !requestBody.isEmpty()) {
			inputRequest.putBody(requestBody);
		}

		UriTemplate template = swaggerService.getUriTemplate(uri, method);

		inputRequest.putAll(template.match(uri));
		inputRequest.putAll(request.getParameterMap());

		log.debug("{} : {}  $input object for template :  {}", method, uri, inputRequest);

		return inputRequest;
	}

	/*
	 * Parsing request Parameters to be sent as header,querystring or path params
	 */
	private void parseRequestParams(CustomHttpRequest request, Input inputRequest,
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

	/*
	 * Used to get RequestBody for backend request
	 */
	public String getRequestBody(CustomHttpRequest request, Input inputRequest) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		String requestBody=request.getRequestBody();
		
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

	/*
	 * Used to build backendrequest url with pathparams and querystring
	 */
	private URI buildRequestUrl(GatewayIntegration integrationObject, Map<String, String> pathParams,
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

//		 Add querystring if exist & rebuilding URI using URIBuilder class else returning targetUri
		if (!querystringParams.isEmpty()) {

			URIBuilder builder = new URIBuilder().setScheme(targetUri.getScheme()).setHost(targetUri.getHost())
					.setPort(targetUri.getPort()).setPath(targetUri.getPath()).setParameters(querystringParams);
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

	/*
	 * Construct $context object from request
	 */
	private GatewayContext getGatewayContext(CustomHttpRequest request) {
		GatewayContext context = new GatewayContext();

		Map<String, String> identity = new LinkedHashMap<String, String>();
		identity.put("cognitoIdentityId", cognitoCacheService.getCognitoId(request));
		identity.put("userAgent", request.getHeader("User-Agent"));

		context.setIdentity(identity);
		context.setHttpMethod(request.getMethod());
		context.setProtocol(request.getProtocol());

		return context;
	}

	/*
	 * Interepret parameter values of backend request
	 * 
	 * it can be - 1. direct value as string enclosed by single quotes - can be used
	 * directly , 2. cogId - can be get by using cognitoCacheService's getCognitoId
	 * Method, 3. any paramvalue from frontend request - can be obtained by Input
	 * object
	 */
	private String interpretParamValue(CustomHttpRequest request, String value, Input inputRequest) {
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

	/*
	 * Returns object of HttpUriRequest provided backendUrl , httpMethod and
	 * requestBody
	 * 
	 * if invalid httpMethod then default is get method
	 */
	private HttpUriRequest getBackendRequest(URI backendUrl, String httpMethod, String parsedRequestBody) {
		StringEntity entity = null;
		switch (httpMethod) {

		case "POST":
			HttpPost httpPost = new HttpPost(backendUrl);
			try {
				entity = new StringEntity(parsedRequestBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPost.setEntity(entity);
			return httpPost;

		case "PUT":
			HttpPut httpPut = new HttpPut(backendUrl);
			try {
				entity = new StringEntity(parsedRequestBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPut.setEntity(entity);
			return httpPut;

		case "PATCH":
			HttpPatch httpPatch = new HttpPatch(backendUrl);
			try {
				entity = new StringEntity(parsedRequestBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPatch.setEntity(entity);
			return httpPatch;

		case "DELETE":
			HttpDelete httpDelete = new HttpDelete(backendUrl);
			return httpDelete;

		case "GET":
		default:
			HttpGet httpGet = new HttpGet(backendUrl);
			return httpGet;

		}
	}

	/*
	 * Used to set request headers (if any) of backendRequest
	 */
	private void setHeaders(CustomHttpRequest request, HttpUriRequest backendRequest,
			Map<String, String> headerParams) {

		if (!headerParams.isEmpty()) {
			for (String param : headerParams.keySet()) {
				backendRequest.addHeader(param, headerParams.get(param));
			}
		}

		backendRequest.setHeader("Accept", "application/json");
		backendRequest.setHeader("Content-type", "application/json");
		backendRequest.setHeader("referer", request.getHeader("referer"));

	}

}
