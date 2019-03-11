package org.jeavio.apigateway.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jeavio.apigateway.model.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayRequestProcessor {

	@Autowired
	Swagger swagger;

	@Autowired
	RequestHandler requestHandler;

	@Autowired
	ResponseHandler responseHandler;
	
	public static Logger log=LoggerFactory.getLogger(GatewayRequestProcessor.class);

	public ResponseEntity<Object> processRequest(HttpServletRequest request, Map<String, String> allParams,
			String requestBody) {

		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		HttpUriRequest backendRequest = requestHandler.createRequest(request, allParams, requestBody);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse backendResponse = null;
		
		try {

			backendResponse = (CloseableHttpResponse) httpclient.execute(backendRequest);

			log.debug("{} : {} Request send to backend and Response obtained", method, uri);
			
		} catch (IOException e) {

			log.error("Error : ",e.getMessage());
		}

		ResponseEntity<Object> response = responseHandler.getResponse(uri,method,backendResponse);
		
		try {
			
			backendResponse.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return response;
	}

}
