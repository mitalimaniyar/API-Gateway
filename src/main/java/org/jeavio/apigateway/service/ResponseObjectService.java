package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.RequestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class ResponseObjectService {

	public static Logger APIGatewayLogger = LoggerFactory.getLogger(ResponseObjectService.class);

	@Autowired
	IntegrationService integrationService;

	@Autowired
	URLMethodService urlMethodService;

	public String getResponseBody(String uri, String method, HttpResponse backendResponse)
			throws ClientProtocolException, IOException {

		APIGatewayLogger.debug(method + " " + uri + " " + " Generating ResponseBody");
		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, status);

		HttpEntity entity = backendResponse.getEntity();
		if (entity == null)
			return null;

		String responseBody = EntityUtils.toString(entity);

		APIGatewayLogger.debug(method + " " + uri + " " + " \n Response Body from Backend: " + responseBody);

//		Parsing templates

		String template = null;
		if (integratedResponse.getResponseTemplates() != null)
			template = integratedResponse.getResponseTemplates().get("application/json");

		if (integratedResponse.getResponseTemplates() == null || template == null
				|| template.equals("__passthrough__")) {

			APIGatewayLogger.debug(method + " " + uri + " " + "\n Template Not Found or \"__passthrough__\" found...");
			APIGatewayLogger.debug("Sending Response Body : " + responseBody);
			return responseBody;
		} else {

			RequestResponse outputResponse = new RequestResponse();
			outputResponse.putBody(responseBody);

			VelocityEngine velocityEngine = new VelocityEngine();
			VelocityContext context = new VelocityContext();

			context.put("input", outputResponse);
			StringWriter writer = new StringWriter();

			if (velocityEngine.evaluate(context, writer, "responseTemplate", template)) {
				APIGatewayLogger.debug(method + " " + uri + " " + "\n Template Found & successfuly merged");
				APIGatewayLogger.debug("Sending Response Body : " + writer.toString());
				return writer.toString();
			} else {
				APIGatewayLogger.debug(method + " " + uri + " " + "\n Template Found & merge failed");
				APIGatewayLogger.debug("Sending No Response Body ");
				return null;
			}
		}
	}

	public HttpHeaders getResponseHeaders(String uri, String method, HttpResponse backendResponse) {

		APIGatewayLogger.debug(method + " " + uri + " " + " Generating ResponseHeaders");

		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, status);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> responseParameters = integratedResponse.getResponseParameters();
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

		APIGatewayLogger.debug(method + " " + uri + " " + "\n Headers to be sent to frontend : " + headers);
		return headers;

	}

	public int getResponseStatus(String uri, String method, HttpResponse backendResponse) {

		APIGatewayLogger.debug(method + " " + uri + " " + " Generating ResponseStatus");

		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, status);

		int statusCode = Integer.parseInt(integratedResponse.getStatusCode());

		APIGatewayLogger.debug(method + " " + uri + " " + " \n Response Code from AWS Backend : " + status
				+ " \nResponse code to be sent to backend : " + statusCode);
		return statusCode;
	}

	private IntegrationResponse getIntegratedResponse(String uri, String method, Integer status) {

		IntegrationResponse integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses()
				.get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get("default");
		}
		return integratedResponse;
	}

}
