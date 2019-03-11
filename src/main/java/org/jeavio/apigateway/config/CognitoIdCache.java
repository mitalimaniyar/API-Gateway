package org.jeavio.apigateway.config;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * CognitoIdMap used to map sessionToken with CognitoId
 * 
 * CognitoId is used in AWS service as usr credential
 */
@Configuration
public class CognitoIdCache {

	@Bean
	public DualHashBidiMap getCognitoIdMap() {
		DualHashBidiMap cognitoIdMap = new DualHashBidiMap();
		return cognitoIdMap;
	}

}
