package org.jeavio.apigateway.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpGet;
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
	
	@RequestMapping("/api/login")
	public String checking(HttpServletRequest request,@RequestParam Map<String,String> allParams,@RequestBody(required=false) String requestBody) {
		
		String uri = request.getRequestURI();
        String method = request.getMethod().toLowerCase();
        
        InputRequest inputRequest=requestObjectService.getInputObject(uri, method, allParams, requestBody);
        String ParsedRequestBody=requestObjectService.getRequestBody(request,inputRequest, requestBody);
        
        if(ParsedRequestBody!=null)
     	   return ParsedRequestBody;
        else 
     	   return "hello world";
	}	
	
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
        cognitoIdMap.put("mytoken", "Demo CogId");
        InputRequest inputRequest=requestObjectService.getInputObject(uri, method, allParams, requestBody);
        String ParsedRequestBody=requestObjectService.getRequestBody(request,inputRequest, requestBody);
        
        
//      HttpMethodObject serviceBody=urlMethodService.parseRequest(uri, method);
       // return serviceBody;
//        return templateService.getRequiredTemplate(uri, method,"requestTemplate").toString();
       // return templateService.getRequiredTemplate(uri, method,"responseTemplate","200").toString();
       if(ParsedRequestBody!=null)
    	   return ParsedRequestBody;
       else 
    	   return "hello world";
       
	}

}
