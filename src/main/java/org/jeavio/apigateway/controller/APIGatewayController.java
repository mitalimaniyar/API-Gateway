package org.jeavio.apigateway.controller;

import java.io.IOException;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
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
	public String UrlMapper(HttpServletRequest request, @RequestParam Map<String, String> allParams,
			@RequestBody(required = false) String requestBody,HttpServletResponse response) {

//		URL Validation
//		String requestHost=request.getHeader("host");
//		String requestScheme=request.getScheme();

//		if(!(swaggerObject.getHost().equals(requestHost) && swaggerObject.getSchemes().contains(requestScheme))) {
//			Error
//		}

		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		InputRequest inputRequest = requestObjectService.getInputObject(uri, method, allParams, requestBody);

		HttpUriRequest requestSend = requestObjectService.createRequest(request, inputRequest, requestBody);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse backendResponse = null;
		ResponseHandler<String> responseHandler=null;
		try {
			
			
			// Create a custom response handler
			responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse myresponse) throws ClientProtocolException, IOException {
                    int status = myresponse.getStatusLine().getStatusCode();
                    response.setContentType("application/json");
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = myresponse.getEntity();
                        
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            
            backendResponse = (CloseableHttpResponse) httpclient.execute(requestSend);
           
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		
		String responseBody=null;
		try {
			responseBody=responseHandler.handleResponse(backendResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        Populate Cognito Id cache
		if (uri.equals("/api/login")) {
			cognitoIdMap.put("mytoken", "Demo CogId");
//        	  cognitoIdMap.put(response.getFirstHeader("sessionToken").getValue(),response.getFirstHeader("identityId").getValue());

		}
		
		response.setContentType("application/json");
		response.addHeader("Baeldung-Example-Header", "Value-HttpServletResponse");
		
		return responseBody;

	}

}
