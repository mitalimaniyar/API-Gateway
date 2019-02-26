package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.RequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ResponseObjectService {
	@Autowired
	HttpServletRequest request;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	URLMethodService urlMethodService;

	public String getResponseBody(HttpResponse backedResponse) throws ClientProtocolException, IOException {
		Integer status = backedResponse.getStatusLine().getStatusCode();

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		IntegrationResponse integratedResponse;
		integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses()
				.get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses()
					.get("default");
			String expectedstatus = integratedResponse.getStatusCode();

			if (!expectedstatus.equals(status.toString()))
				throw new ClientProtocolException("Unexpected response status: " + status);

		}

		HttpEntity entity = backedResponse.getEntity();
		if (entity == null)
			return null;

//		Parsing templates
		String template = null;
		if (integratedResponse.getResponseTemplates() != null)
			template = integratedResponse.getResponseTemplates().get("application/json");
		String responseBody=EntityUtils.toString(entity);
		if (template == null)
			return null;
		else if (template.equals("__passthrough__"))
			return responseBody;
		else {
			ObjectMapper objectMapper = new ObjectMapper();
			RequestResponse outputResponse = objectMapper.readValue(responseBody.getBytes(),
					RequestResponse.class);

			VelocityEngine velocityEngine = new VelocityEngine();
			VelocityContext context = new VelocityContext();

			context.put("input", outputResponse);
			StringWriter writer = new StringWriter();

			if (velocityEngine.evaluate(context, writer, "responseTemplate", template))
				return writer.toString();
			else
				return null;
		}
	}

}
