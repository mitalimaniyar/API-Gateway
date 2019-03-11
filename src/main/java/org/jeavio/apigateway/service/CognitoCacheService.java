package org.jeavio.apigateway.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CognitoCacheService {
	@Autowired
	DualHashBidiMap cognitoIdMap;

	private JSONParser parser = new JSONParser();
	
	public static Logger log = LoggerFactory.getLogger(CognitoCacheService.class);

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

	public String getCognitoId(HttpServletRequest request) {
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
