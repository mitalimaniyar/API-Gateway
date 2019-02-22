package org.jeavio.apigateway.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.SwaggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
	
	
	@Autowired
	SwaggerService swaggerService;

	@Bean
	public Swagger getSwagger() {
		Swagger swagger = swaggerService.parse("/home/jeavio64/Downloads/swagger-update-effective-staging.json");
		return swagger;
	}	
	
	@Bean
    public Map<String, String> getCognitoIdMap() {
        Map<String,String> CognitoIdMap=new ConcurrentHashMap<String, String>();
        return CognitoIdMap;
    }
	
}
