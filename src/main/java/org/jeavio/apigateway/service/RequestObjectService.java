package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
//import org.apache.velocity.app.VelocityEngine;
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
	
	ObjectMapper objectMapper=new ObjectMapper();
	
	public InputRequest getInputObject(String uri,String method,Map<String,String> allParams,String requestBody) {
		InputRequest inputRequest=new InputRequest();
		if(requestBody!=null && !requestBody.isEmpty()) {
    		try {
    			inputRequest = objectMapper.readValue(requestBody.getBytes(),InputRequest.class);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
		
		UriTemplate temp=null;
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
	
	
	public String getRequestBody(String uri,String method,InputRequest inputRequest,String requestBody) {
		UriTemplate uriTemplate=new UriTemplate("/");
		VelocityEngine velocityEngine=new VelocityEngine();
		try {
			uriTemplate=urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GatewayIntegration integrationObject=integrationService.getIntegrationObject(uriTemplate.toString(), method);
		
//		Template requestTemplate;
		if(integrationObject.getRequestTemplates()!=null && integrationObject.getRequestTemplates().get("application/json")!=null) {
			if(integrationObject.getRequestTemplates().get("application/json").equals("__passthrough__")) {
				return requestBody;
			}
			else{
				
//				requestTemplate=templateService.getRequiredTemplate(uri, method,"requestTemplate");
//				
//				requestTemplate=ve.getTemplate("sample.vm");
//				
//				if(requestTemplate!=null) {
//						
//				       
//				        
//				        
//				        requestTemplate.merge(context, writer);
//				        
//						return writer.toString();
//				}
//				else
//					return null;
				VelocityContext context=new VelocityContext();
				 context.put("input",inputRequest);
				StringWriter writer=new StringWriter();
				String template=integrationObject.getRequestTemplates().get("application/json");
				velocityEngine.evaluate(context, writer, "requestTemplate", template);
				return writer.toString();
			}
			
		}
		else
			return null;
	}

}
