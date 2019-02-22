package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.InputRequest;
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

	public InputRequest getInputObject(String uri, String method, Map<String, String> allParams, String requestBody) {
		InputRequest inputRequest = new InputRequest();
		if (requestBody != null && !requestBody.isEmpty()) {
			try {
				inputRequest = objectMapper.readValue(requestBody.getBytes(), InputRequest.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	public String getRequestBody(HttpServletRequest request, InputRequest inputRequest, String requestBody) {
		
		String uri = request.getRequestURI();
        String method = request.getMethod().toLowerCase();
        
		UriTemplate uriTemplate = new UriTemplate("/");
		VelocityEngine velocityEngine = new VelocityEngine();
		try {
			uriTemplate = urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GatewayIntegration integrationObject = integrationService.getIntegrationObject(uriTemplate.toString(), method);

//		Template requestTemplate;
		if (integrationObject.getRequestTemplates() != null
				&& integrationObject.getRequestTemplates().get("application/json") != null) {
			if (integrationObject.getRequestTemplates().get("application/json").equals("__passthrough__")) {
				return requestBody;
			} else {
				
				VelocityContext context = new VelocityContext();
				if(request.getHeader("x-amz-security-token")!=null) {
		        	context.put("context", getContextObject(request));
		        }

				context.put("input", inputRequest);
				StringWriter writer = new StringWriter();
				String template = integrationObject.getRequestTemplates().get("application/json");
				boolean status = velocityEngine.evaluate(context, writer, "requestTemplate", template);
				if (status)
					return writer.toString();
				else
					return null;
			}

		} else
			return null;
	}

	public  String getCognitoId(HttpServletRequest request) {
			String sessionToken=request.getHeader("x-amz-security-token");
			String cognitoId=null;
			if(sessionToken!=null && cognitoIdMap.containsKey(sessionToken)) {
				cognitoId=cognitoIdMap.get(sessionToken);
			}
			return cognitoId;
	}
	
	private  Map<String, Map<String, String>> getContextObject(HttpServletRequest request){
		Map<String, Map<String, String>> context1 = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> identity = new LinkedHashMap<String, String>();
		identity.put("cognitoIdentityId", getCognitoId(request));
		context1.put("identity", identity);
		
		return context1;
	}

}
