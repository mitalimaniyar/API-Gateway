package org.jeavio.apigateway.service;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.jeavio.apigateway.model.CustomHttpRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
/*
 * This class provides an interface to communicate with CogIdMap
 * 
 * 1.populateCognitoCache : used to add new entry or update sessionToken in cognitoCache
 *                          on specific url requests, here "api/login" && "api/refreshCredentials"
 * 2.addEntey : get JsonObject containig sessionToken & cogId from `populateCognitoCache` method
 * 				and add/update it in cogIdMap
 * 3.getCognitoId : get CogId of specific sessionToken
 * 
 */
@Service
//@Slf4j
public class CognitoCacheService {
	private static final Logger log = LoggerFactory.getLogger("API");
	@Autowired
	DualHashBidiMap cognitoIdMap;

	private JSONParser parser = new JSONParser();

	public void populateCognitoCache(String uri, String responseBody) {
		try {
			if (uri.equals("/api/login")) {

				JSONObject credentials = (JSONObject) ((JSONObject) parser.parse(responseBody)).get("credentials");
				addEntry(credentials);

			} else if (uri.equals("/api/refreshCredentials")) {

				JSONObject credentials = (JSONObject) ((JSONObject) parser.parse(responseBody));
				addEntry(credentials);
			}

		} catch (ParseException e) {
			
			log.error("Error :"+e.getMessage());
		}

	}

	private void addEntry(JSONObject credentials) {
		
		String sessionToken = (String) credentials.get("sessionToken");
		String cogId = (String) credentials.get("identityId");
		cognitoIdMap.put(sessionToken, cogId);		
	}

	public String getCognitoId(CustomHttpRequest request) {
		String sessionToken = request.getHeader("x-amz-security-token");
		String cognitoId = null;
		if (sessionToken != null && cognitoIdMap.containsKey(sessionToken)) {
	
			cognitoId = (String)cognitoIdMap.get(sessionToken);
		}
	
		log.debug("{} : {}  CogId : {} sessionToken :  {} ", request.getMethod(), request.getRequestURI(), cognitoId,
				sessionToken);
		return cognitoId;
	}

}
