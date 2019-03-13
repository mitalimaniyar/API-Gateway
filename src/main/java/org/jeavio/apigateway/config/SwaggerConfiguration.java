package org.jeavio.apigateway.config;

import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.SwaggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SwaggerConfiguration {

	@Autowired
	SwaggerService swaggerService;
	
	@Value("${swagger.path}")
	private String swaggerPath;
	
	@Bean
	public Swagger getSwagger() {

//		String filename = "/home/jeavio64/Downloads/swagger-update-qa.json";
		
		String swaggerSource=swaggerPath;
		Swagger swagger = swaggerService.parse(swaggerSource);

		log.info("Swagger File Source : " + swaggerSource);

		return swagger;
	}
}

