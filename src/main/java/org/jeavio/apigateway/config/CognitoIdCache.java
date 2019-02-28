package org.jeavio.apigateway.config;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoIdCache {

	@Bean
	public DualHashBidiMap getCognitoIdMap() {
		DualHashBidiMap cognitoIdMap = new DualHashBidiMap();
		return cognitoIdMap;
	}
    
}
