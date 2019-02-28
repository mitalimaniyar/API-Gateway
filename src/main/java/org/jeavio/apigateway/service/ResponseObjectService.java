package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ResponseObjectService {

	@Autowired
	IntegrationService integrationService;

	@Autowired
	URLMethodService urlMethodService;

	public String getResponseBody(HttpServletRequest request, HttpResponse backendResponse) throws ClientProtocolException, IOException {
		Integer status = backendResponse.getStatusLine().getStatusCode();

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		IntegrationResponse integratedResponse;
		integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get("default");
		}

		HttpEntity entity = backendResponse.getEntity();
		if (entity == null)
			return null;

//		Parsing templates
		String template = null;
		if (integratedResponse.getResponseTemplates() != null)
			template = integratedResponse.getResponseTemplates().get("application/json");
		String responseBody = EntityUtils.toString(entity);

		if (template == null || template.equals("__passthrough__")) {

			return responseBody;
	}
		else {
			
			RequestResponse outputResponse = new RequestResponse();
			outputResponse.putBody(responseBody);
			
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

	public HttpHeaders getResponseHeaders(String uri,String method,HttpResponse backendResponse) {
			
		Integer status = backendResponse.getStatusLine().getStatusCode();
		
		IntegrationResponse integratedResponse;
		integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get("default");
		}

		HttpHeaders headers=new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	
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
			if(!paramName.equals("Access-Control-Allow-Origin"))
				headers.add(paramName, paramValue);
		}
		return headers;
		
	}
	
	public int getResponseStatus(String uri,String method,HttpResponse backendResponse) {
		Integer status = backendResponse.getStatusLine().getStatusCode();
		
		IntegrationResponse integratedResponse;
		integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get(status.toString());
		if (integratedResponse == null) {
			integratedResponse = integrationService.getIntegrationObject(uri, method).getResponses().get("default");
		}
		
		return Integer.parseInt(integratedResponse.getStatusCode());
	}

}
