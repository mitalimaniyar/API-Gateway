package org.jeavio.apigateway.controller;

import java.io.IOException;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeavio.apigateway.model.InputRequest;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.RequestObjectService;
import org.jeavio.apigateway.service.URLMethodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class APIGatewayController {
	
	@Autowired
	Swagger swagger;
	
	@Autowired
	URLMethodService urlMethodService;
	
	@Autowired
	RequestObjectService requestObjectService;
	
	@Autowired
	Map<String, String> cognitoIdMap;
	
	
	@RequestMapping
	public String UrlMapper(HttpServletRequest request,@RequestParam Map<String,String> allParams,@RequestBody(required=false) String requestBody) {
		
//		URL Validation
//		String requestHost=request.getHeader("host");
//		String requestScheme=request.getScheme();
		
//		if(!(swaggerObject.getHost().equals(requestHost) && swaggerObject.getSchemes().contains(requestScheme))) {
//			Error
//		}
		
		//URL parsing
        String uri = request.getRequestURI();
        String method = request.getMethod().toLowerCase();
      
        InputRequest inputRequest=requestObjectService.getInputObject(uri, method, allParams, requestBody);
        
        HttpUriRequest requestSend=requestObjectService.createRequest(request, inputRequest, requestBody);
        requestSend.setHeader("Accept", "application/json");
		requestSend.setHeader("Content-type", "application/json");
		if (request.getHeader("referer") != null)
			requestSend.setHeader("referer", request.getHeader("referer"));
		
        HttpClient httpclient = HttpClients.createDefault();
        HttpResponse response=null;
        try {
			response= httpclient.execute(requestSend);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
//        Populate Cognito Id cache
        if(uri.equals("/api/login")) {
        	  cognitoIdMap.put("mytoken", "Demo CogId");
//        	  cognitoIdMap.put(response.getFirstHeader("sessionToken").getValue(),response.getFirstHeader("identityId").getValue());
        	  
        }
        
        String ret=null;
        try {
			ret= EntityUtils.toString(response.getEntity()) ;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
        return ret;
//       
	}

}
