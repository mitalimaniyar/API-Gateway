package org.jeavio.apigateway.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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



//@Bean
//public Map<String, String> getCognitoIdMap() {
//    Map<String,String> CognitoIdMap=new ConcurrentHashMap<String, String>();
//    return CognitoIdMap;
//	}