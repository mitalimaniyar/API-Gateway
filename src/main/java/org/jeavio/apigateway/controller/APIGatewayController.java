package org.jeavio.apigateway.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jeavio.apigateway.Templates;
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.HttpMethodService;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.URLMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

@RestController
public class APIGatewayController {
	
	@Autowired
	Swagger swagger;
	
	@Autowired
	Templates template;
	
	@Autowired
	URLMethodService urlMethodService;
	
	@RequestMapping
	public HttpMethodService UrlMapper(HttpServletRequest request) {
		
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
        HttpMethodService serviceBody=urlMethodService.parseRequest(uri, method);
        return serviceBody;
        
	}

}
