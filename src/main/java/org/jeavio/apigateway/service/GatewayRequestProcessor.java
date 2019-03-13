package org.jeavio.apigateway.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jeavio.apigateway.model.CustomHttpRequest;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GatewayRequestProcessor {

	@Autowired
	Swagger swagger;

	@Autowired
	RequestHandler requestHandler;

	@Autowired
	ResponseHandler responseHandler;
	
	@Autowired
	CustomRequestCreator customRequestCreator;

	public ResponseEntity<Object> processRequest(HttpServletRequest servletRequest, Map<String, String> allParams,
			String requestBody) {

		// URL parsing
		String uri = servletRequest.getRequestURI();
		String method = servletRequest.getMethod().toLowerCase();
		
		CustomHttpRequest request=customRequestCreator.parseIncomingRequest(servletRequest, allParams, requestBody);

		HttpUriRequest backendRequest = requestHandler.createRequest(request);

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
