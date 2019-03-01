package org.jeavio.apigateway.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jeavio.apigateway.model.RequestResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.IntegrationService;
import org.jeavio.apigateway.service.RequestObjectService;
import org.jeavio.apigateway.service.ResponseObjectService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.PATCH, RequestMethod.POST, RequestMethod.PUT}, allowedHeaders = "*")
public class APIGatewayController {

	@Autowired
	Swagger swagger;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	RequestObjectService requestObjectService;

	@Autowired
	DualHashBidiMap cognitoIdMap;

	@Autowired
	ResponseObjectService responseObjectService;

	public static Logger log = LoggerFactory.getLogger(APIGatewayController.class);

	@RequestMapping(produces = { "application/json" })
	public ResponseEntity<Object> UrlMapper(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, String> allParams, @RequestBody(required = false) String requestBody) {

		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		log.debug("Request from Frontend : {} : {}", method, uri);

		RequestResponse inputRequest = requestObjectService.getInputObject(uri, method, allParams, requestBody);

		HttpUriRequest requestSend = requestObjectService.createRequest(request, inputRequest, requestBody);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse backendResponse = null;
		String responseBody = null;
		HttpHeaders headers = null;
		int responseStatus = 200;

		try {

			backendResponse = (CloseableHttpResponse) httpclient.execute(requestSend);

			log.debug("{} : {} Request send to backend and Response obtained", method, uri);

			responseBody = responseObjectService.getResponseBody(uri, method, backendResponse);
			headers = responseObjectService.getResponseHeaders(uri, method, backendResponse);
			responseStatus = responseObjectService.getResponseStatus(uri, method, backendResponse);

		} catch (IOException e1) {

			e1.printStackTrace();
		}

//      Populate Cognito Id cache
		if (uri.equals("/api/login")) {

			try {

				JSONParser parser = new JSONParser();
				JSONObject credentials = (JSONObject) ((JSONObject) parser.parse(responseBody)).get("credentials");

				String sessionToken = (String) credentials.get("sessionToken");
				String cogId = (String) credentials.get("identityId");
				cognitoIdMap.put(sessionToken, cogId);

				log.debug("{} : {}  On Login Request manipulating CogIdMap for cogId : {}", method, uri, cogId);

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

				String sessionToken = (String) credentials.get("sessionToken");
				String cogId = (String) credentials.get("identityId");
				cognitoIdMap.put(sessionToken, cogId);

				log.debug("{} : {} Refreshing Credentials : Generating new sessionToken for cogId :  {}", method, uri,
						cogId);

			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		ResponseEntity<Object> res = ResponseEntity.status(responseStatus).headers(headers).body(responseBody);

		log.debug("{} : {}  Sending request to frontend ", method, uri);
		return res;

	}
}
