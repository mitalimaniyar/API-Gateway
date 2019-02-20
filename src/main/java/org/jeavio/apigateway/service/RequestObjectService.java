package org.jeavio.apigateway.service;

import java.io.IOException;
import java.util.Map;

import org.jeavio.apigateway.model.InputRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RequestObjectService {
	
	@Autowired
	URLMethodService urlMethodService;
	
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

}
