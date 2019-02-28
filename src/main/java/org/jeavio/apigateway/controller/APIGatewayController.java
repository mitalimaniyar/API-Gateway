package org.jeavio.apigateway.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.RequestResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.IntegrationService;
import org.jeavio.apigateway.service.RequestObjectService;
import org.jeavio.apigateway.service.ResponseObjectService;
import org.jeavio.apigateway.service.URLMethodService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class APIGatewayController {

	@Autowired
	Swagger swagger;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	RequestObjectService requestObjectService;

	@Autowired
	Map<String, String> cognitoIdMap;

	@Autowired
	ResponseObjectService responseObjectService;


	@RequestMapping(produces = { "application/json" })
	public ResponseEntity<Object> UrlMapper(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, String> allParams,
			@RequestBody(required = false) String requestBody) {


		String responseBody = null;

		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		RequestResponse inputRequest = requestObjectService.getInputObject(uri, method, allParams, requestBody);

		HttpUriRequest requestSend = requestObjectService.createRequest(request, inputRequest, requestBody);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse backendResponse = null;
		HttpHeaders headers=null;
		int responseStatus=200;

		try {

			backendResponse = (CloseableHttpResponse) httpclient.execute(requestSend);
			responseBody = responseObjectService.getResponseBody(request, backendResponse);
			headers=responseObjectService.getResponseHeaders(uri,method,backendResponse);
			responseStatus=responseObjectService.getResponseStatus(uri,method,backendResponse);

		} catch (IOException e1) {

			e1.printStackTrace();
		}

//      Populate Cognito Id cache
		if (uri.equals("/api/login")) {

			try {


				JSONParser parser = new JSONParser();
				JSONObject credentials = (JSONObject) ((JSONObject) parser.parse(responseBody)).get("credentials");

				cognitoIdMap.put((String) credentials.get("sessionToken"), (String) credentials.get("identityId"));

			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (uri.equals("/api/refreshCredentials")) {

			try {

				JSONParser parser = new JSONParser();
				JSONObject credentials = (JSONObject) ((JSONObject) parser.parse(responseBody));

				cognitoIdMap.put((String) credentials.get("sessionToken"), (String) credentials.get("identityId"));

			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		ResponseEntity<Object> res = ResponseEntity.status(responseStatus).headers(headers).body(responseBody);
		return res;

	}
}
