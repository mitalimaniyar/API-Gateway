package org.jeavio.apigateway.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeavio.apigateway.service.GatewayRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT}, allowedHeaders = "*")
@Slf4j
public class APIGatewayController {

	@Autowired
	GatewayRequestProcessor requestProcessor;

	@RequestMapping(produces = { "application/json" })
	public ResponseEntity<Object> processRequest(HttpServletRequest request,
			@RequestParam Map<String, String> allParams, @RequestBody(required = false) String requestBody) {

		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		log.debug("Request from Frontend : {} : {}", method, uri);
		
		ResponseEntity<Object> response=requestProcessor.processRequest(request,allParams,requestBody);

		log.debug("{} : {}  Sending request to frontend ", method, uri);
		
		return response;

	}
}
