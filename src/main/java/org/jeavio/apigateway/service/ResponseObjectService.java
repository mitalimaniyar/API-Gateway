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
import org.apache.velocity.app.event.EventCartridge;
import org.jeavio.apigateway.EventHandler.VTLInvalidReferenceEventHandler;
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

	public static Logger log = LoggerFactory.getLogger(ResponseObjectService.class);

	@Autowired
	IntegrationService integrationService;

	@Autowired
	URLMethodService urlMethodService;

	public String getResponseBody(String uri, String method, HttpResponse backendResponse)
			throws ClientProtocolException, IOException {

		log.debug("{} : {}  Generating ResponseBody", method, uri);
		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, status);

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

			RequestResponse outputResponse = new RequestResponse();
			outputResponse.putBody(responseBody);

			VelocityEngine velocityEngine = new VelocityEngine();
			VelocityContext context = new VelocityContext();

			context.put("input", outputResponse);
			StringWriter writer = new StringWriter();

			 EventCartridge eventCartridge = new EventCartridge();
			 eventCartridge.addInvalidReferenceEventHandler(new VTLInvalidReferenceEventHandler());
			 eventCartridge.attachToContext(context);
			 
			if (velocityEngine.evaluate(context, writer, "responseTemplate", template)) {
				log.debug("{} : {}  Template Found & successfuly merged", method, uri);
				log.debug("{} : {}  Sending Response Body :  {}", method, uri, writer.toString());
				return writer.toString();
			} else {
				log.debug("{} : {}  Template Found & merge failed", method, uri);
				log.debug("{} : {} Sending No Response Body ", method, uri);
				return null;
			}
		}
	}

	public HttpHeaders getResponseHeaders(String uri, String method, HttpResponse backendResponse) {

		log.debug("{} : {}  Generating ResponseHeaders", method, uri);

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

		log.debug("{} : {}  Headers to be sent to frontend :  {}", method, uri, headers);
		return headers;

	}

	public int getResponseStatus(String uri, String method, HttpResponse backendResponse) {

		log.debug("{} : {}  Generating ResponseStatus", method, uri);

		Integer status = backendResponse.getStatusLine().getStatusCode();

		IntegrationResponse integratedResponse = getIntegratedResponse(uri, method, status);

		int statusCode = Integer.parseInt(integratedResponse.getStatusCode());

		log.debug("{} : {} Response Code from AWS Backend : {}  Response code to be sent to backend :  {}", method, uri,
				status, statusCode);
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
