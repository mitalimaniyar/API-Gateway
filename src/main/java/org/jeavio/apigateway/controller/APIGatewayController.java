package org.jeavio.apigateway.controller;

import javax.servlet.http.HttpServletRequest;

import org.jeavio.apigateway.model.HttpMethodObject;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.TemplateService;
import org.jeavio.apigateway.service.URLMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONArray;

@RestController
public class APIGatewayController {
	
	@Autowired
	Swagger swagger;
	
	@Autowired
	URLMethodService urlMethodService;
	
	@Autowired
	TemplateService templateService;
	
	@RequestMapping
	public String UrlMapper(HttpServletRequest request) {
		
		//URL Validation
//		String requestHost=request.getHeader("host");
//		String requestScheme=request.getScheme();
		
//		if(!(swaggerObject.getHost().equals(requestHost) && swaggerObject.getSchemes().contains(requestScheme))) {
//			HttpMethodService service= new HttpMethodService();
//        	service.set("402", "Forbidden error");
//        	return service;
//		}
		
		//URL parsing
        String uri = request.getRequestURI();
        String method = request.getMethod().toLowerCase();
        
        HttpMethodObject serviceBody=urlMethodService.parseRequest(uri, method);
       // return serviceBody;
        return templateService.getRequiredTemplate(uri, method,"requestTemplate").toString();
       // return templateService.getRequiredTemplate(uri, method,"responseTemplate","200").toString();
        
	}

}
