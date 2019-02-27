package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	IntegrationService integrationService;

	@Autowired
	URLMethodService urlMethodService;

	public String getResponseBody(HttpServletRequest request, HttpResponse backendResponse,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		Integer status = backendResponse.getStatusLine().getStatusCode();

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		IntegrationResponse integratedResponse;
		integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get("default");
		}

//		Setting Response Headers
		//setResponse(response, backendResponse, integratedResponse);

		HttpEntity entity = backendResponse.getEntity();
		if (entity == null)
			return null;

//		Parsing templates
		String template = null;
		if (integratedResponse.getResponseTemplates() != null)
			template = integratedResponse.getResponseTemplates().get("application/json");
		String responseBody = EntityUtils.toString(entity);
		if (template == null || template.equals("__passthrough__")) 
			return responseBody;
		else {
			ObjectMapper objectMapper = new ObjectMapper();
			RequestResponse outputResponse = objectMapper.readValue(responseBody.getBytes(), RequestResponse.class);

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

	private void setResponse(HttpServletResponse response, HttpResponse backendResponse,
			IntegrationResponse integratedResponse) {
			
		int status = Integer.parseInt(integratedResponse.getStatusCode());
		response.setStatus(status);
		
		Map<String, String> responseParameters = integratedResponse.getResponseParameters();
		String paramName,paramValue,value ;
		for(String responseParameter : responseParameters.keySet()) {
			paramName = responseParameter.substring(responseParameter.lastIndexOf(".") + 1);
			value=responseParameters.get(responseParameter);
			if (value.indexOf("'") != -1) {
				paramValue = value.replace("'", "");
			} else {
				String name = value.substring(value.lastIndexOf(".") + 1);
				if(backendResponse.getFirstHeader(name)!=null)
					paramValue =  backendResponse.getFirstHeader(name).getValue();
				else
					paramValue = new String("Not found");
			}
			
			response.addHeader(paramName, paramValue);
		}
		
	}

}
