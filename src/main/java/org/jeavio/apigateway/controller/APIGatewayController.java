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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

//	@RequestMapping(produces = { "application/json" }, method = RequestMethod.OPTIONS)
//	@CrossOrigin(origins = "http://localhost:3000")
//	public ResponseEntity<String> UrlOptions(HttpServletRequest request) {
//
//		// URL parsing
//		String uri = request.getRequestURI();
//		String method = request.getMethod().toLowerCase();
//		
//		HttpHeaders headers=responseObjectService.setResponseHeaders(uri,method,null);
//		
//		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(null);
//		
//	}

	@RequestMapping(produces = { "application/json" })
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Object> UrlMapper(HttpServletRequest request, @RequestParam Map<String, String> allParams,
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

		try {

			backendResponse = (CloseableHttpResponse) httpclient.execute(requestSend);
			responseBody = responseObjectService.getResponseBody(request, backendResponse, null);
			headers=responseObjectService.setResponseHeaders(uri,method,backendResponse);

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
		int responseStatus=backendResponse.getStatusLine().getStatusCode();
		
		
		return ResponseEntity.status(responseStatus).body(responseBody);

	}

}
