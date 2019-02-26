package org.jeavio.apigateway.controller;

import java.io.IOException;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jeavio.apigateway.model.RequestResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.RequestObjectService;
import org.jeavio.apigateway.service.ResponseObjectService;
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
	
	@Autowired
	ResponseObjectService responseObjectService;

	@RequestMapping
	public String UrlMapper(HttpServletRequest request, @RequestParam Map<String, String> allParams,
			@RequestBody(required = false) String requestBody,HttpServletResponse response) {

//		URL Validation
//		String requestHost=request.getHeader("host");
//		String requestScheme=request.getScheme();

//		if(!(swaggerObject.getHost().equals(requestHost) && swaggerObject.getSchemes().contains(requestScheme))) {
//			Error
//		}

		response.setContentType("application/json");
		String responseBody=null;
		
		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		RequestResponse inputRequest = requestObjectService.getInputObject(allParams, requestBody);

		HttpUriRequest requestSend = requestObjectService.createRequest(inputRequest, requestBody);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse backendResponse = null;

		try {
            
            backendResponse = (CloseableHttpResponse) httpclient.execute(requestSend);
           
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		
//      Populate Cognito Id cache
		if (uri.equals("/api/login")) {
			cognitoIdMap.put("mytoken", "Demo CogId");
//      	  cognitoIdMap.put(response.getFirstHeader("sessionToken").getValue(),response.getFirstHeader("identityId").getValue());

		}
		
		
		try {
			responseBody=responseObjectService.getResponseBody(backendResponse);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
//		response.setStatus(400);
		return responseBody;

	}

}
