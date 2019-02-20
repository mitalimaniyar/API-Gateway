package org.jeavio.apigateway.controller;

import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import org.jeavio.apigateway.model.InputRequest;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.RequestObjectService;
import org.jeavio.apigateway.service.TemplateService;
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
	TemplateService templateService;
	
	@Autowired
	RequestObjectService requestObjectService;
	
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
       
		VelocityContext context=new VelocityContext();
        context.put("input",inputRequest);
//      Template t=templateService.getRequiredTemplate(uri, method,"requestTemplate");
        VelocityEngine velocityEngine=new VelocityEngine();
        Template t=velocityEngine.getTemplate("sample.vm");
        StringWriter writer=new StringWriter();
        t.merge(context, writer);
        System.out.print(writer.toString());
        
//        #end
        
//      HttpMethodObject serviceBody=urlMethodService.parseRequest(uri, method);
       // return serviceBody;
        return templateService.getRequiredTemplate(uri, method,"requestTemplate").toString();
       // return templateService.getRequiredTemplate(uri, method,"responseTemplate","200").toString();
       
	}

}
