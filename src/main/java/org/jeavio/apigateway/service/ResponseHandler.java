package org.jeavio.apigateway.service;

import java.io.IOException;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseHandler {

	public static Logger log = LoggerFactory.getLogger(ResponseHandler.class);

	@Autowired
	SwaggerService swaggerService;

	@Autowired
	VelocityTemplateHandler velocityTemplateHandler;

	@Autowired
	CognitoCacheService cognitoCacheService;

	/*
	 * Getting Response Object to be sent
	 */
	public ResponseEntity<Object> getResponse(String uri, String method, HttpResponse backendResponse) {

		String responseBody = null;
		HttpHeaders headers = null;
		int responseStatus = 200;

		try {
			responseStatus = getResponseStatus(uri, method, backendResponse);
			responseBody = getResponseBody(uri, method, backendResponse);
			headers = getResponseHeaders(uri, method, backendResponse);
		} catch (Exception e) {
			log.error("Error : ", e.getMessage());
		}

//      Populate Cognito Id cache
		cognitoCacheService.populateCognitoCache(uri, responseBody);

		ResponseEntity<Object> response = ResponseEntity.status(responseStatus).headers(headers).body(responseBody);

		return response;
	}
	
	/*
	 * To get Response body to be sent
	 */
	public String getResponseBody(String uri, String method, HttpResponse backendResponse)
			throws ClientProtocolException, IOException {

		log.debug("{} : {}  Generating ResponseBody", method, uri);

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, backendResponse);

		HttpEntity entity = backendResponse.getEntity();
		if (entity == null)
			return null;

		String responseBody = EntityUtils.toString(entity);

		log.debug("{} : {}  Response Body from Backend: {}", method, uri, responseBody);

//		Parsing templates

		String template = null;
		if (integratedResponse.getResponseTemplates() != null)
			template = integratedResponse.getResponseTemplates().get("application/json");

		if (integratedResponse.getResponseTemplates() == null || template == null
				|| template.equals("__passthrough__")) {

			log.debug("{} : {}  Template Not Found or \"__passthrough__\" found...", method, uri);
			log.debug("{} : {}  Sending Response Body :  {}", method, uri, responseBody);

			return responseBody;
		} else {

			Input outputResponse = new Input();

			outputResponse.putBody(responseBody);

			String body = velocityTemplateHandler.processTemplate(uri, method, template, outputResponse, null);

			return body;
		}
	}

	/*
	 * Get Response Headers to be sent
	 */
	public HttpHeaders getResponseHeaders(String uri, String method, HttpResponse backendResponse) {

		log.debug("{} : {}  Generating ResponseHeaders", method, uri);

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, backendResponse);
		Map<String, String> responseParameters = integratedResponse.getResponseParameters();

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		String paramName, paramValue, value;

		for (String responseParameter : responseParameters.keySet()) {

			paramName = responseParameter.substring(responseParameter.lastIndexOf(".") + 1);
			value = responseParameters.get(responseParameter);

			if (value.indexOf("'") != -1) {
				paramValue = value.replace("'", "");
			} else {
				String name = value.substring(value.lastIndexOf(".") + 1);
				if (backendResponse.getFirstHeader(name) != null)
					paramValue = backendResponse.getFirstHeader(name).getValue();
				else
					paramValue = new String("Not found");
			}

			if (!paramName.equals("Access-Control-Allow-Origin"))
				headers.add(paramName, paramValue);
		}

		log.debug("{} : {}  Headers to be sent to frontend :  {}", method, uri, headers);
		return headers;

	}

	/*
	 * Getting response status to be sent
	 */
	public int getResponseStatus(String uri, String method, HttpResponse backendResponse) {

		log.debug("{} : {}  Generating ResponseStatus", method, uri);

		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, backendResponse);

		int statusCode = Integer.parseInt(integratedResponse.getStatusCode());

		log.debug("{} : {} Response Code from AWS Backend : {}  Response code to be sent to backend :  {}", method, uri,
				status, statusCode);
		return statusCode;
	}

	/*
	 * Getting IntegrationResponse object from swagger providing uri,method
	 * and backendResponse
	 */
	private IntegrationResponse getIntegratedResponse(String uri, String method, HttpResponse backendResponse) {

		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = swaggerService.getGatewayIntegration(uri, method).getResponses()
				.get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = swaggerService.getGatewayIntegration(uri, method).getResponses().get("default");
		}
		return integratedResponse;
	}

}
