package org.jeavio.apigateway.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	@RequestMapping(produces = { "application/json" })
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Object> UrlMapper(HttpServletRequest request, @RequestParam Map<String, String> allParams,
				@RequestBody(required = false) String requestBody) {
		// , HttpServletResponse response) {

		String responseBody = null;

		// URL parsing
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		RequestResponse inputRequest = requestObjectService.getInputObject(uri, method, allParams, requestBody);

		HttpUriRequest requestSend = requestObjectService.createRequest(request, inputRequest, requestBody);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse backendResponse = null;

		try {

			backendResponse = (CloseableHttpResponse) httpclient.execute(requestSend);

		} catch (IOException e1) {

			e1.printStackTrace();
		}

//      Populate Cognito Id cache
		if (uri.equals("/api/login")) {
//			cognitoIdMap.put("mytoken", "Demo CogId");
			cognitoIdMap.put(backendResponse.getFirstHeader("sessionToken").getValue(),
					backendResponse.getFirstHeader("identityId").getValue());

		}

		try {
			responseBody = responseObjectService.getResponseBody(request, backendResponse, null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseBody);
		

	}

}
