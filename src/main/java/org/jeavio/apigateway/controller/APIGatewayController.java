package org.jeavio.apigateway.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
	
//	@RequestMapping("/api/login")
//	public String checking(HttpServletRequest request,@RequestParam Map<String,String> allParams,@RequestBody(required=false) String requestBody) {
//		
//		String uri = request.getRequestURI();
//        String method = request.getMethod().toLowerCase();
//        
//        InputRequest inputRequest=requestObjectService.getInputObject(uri, method, allParams, requestBody);
//        String ParsedRequestBody=requestObjectService.getRequestBody(request,inputRequest, requestBody);
//        cognitoIdMap.put("mytoken2", "Demo CogId");
//        if(ParsedRequestBody!=null)
//     	   return ParsedRequestBody;
//        else 
//     	   return "hello world";
//	}	
	
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
        
        /*
         * Request send and response received
         * 
         * 
         */
       
        String url=requestObjectService.createRequest(request, inputRequest, requestBody);
        
//        HttpGet httpget = new HttpGet("http://localhost:9090/parse");
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpResponse response=null;
//        try {
//			response= httpclient.execute(httpget);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        if(uri.equals("/api/login")) {
        	  cognitoIdMap.put("mytoken", "Demo CogId");
        	  
        }
        
        return url;
        
//      HttpMethodObject serviceBody=urlMethodService.parseRequest(uri, method);
//        return serviceBody;
//        return templateService.getRequiredTemplate(uri, method,"requestTemplate").toString();
//        return templateService.getRequiredTemplate(uri, method,"responseTemplate","200").toString();
//        String ret=null;
//        try {
//			ret= EntityUtils.toString(response.getEntity()) ;
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//      
//        return ret;
//       
	}

}
