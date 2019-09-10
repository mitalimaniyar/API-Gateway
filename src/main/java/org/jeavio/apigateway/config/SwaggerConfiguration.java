package org.jeavio.apigateway.config;

import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.SwaggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
//@Slf4j
public class SwaggerConfiguration {

	private static final Logger log = LoggerFactory.getLogger("API");
	@Autowired
	SwaggerService swaggerService;

	public static Logger APIGatewayLogger = LoggerFactory.getLogger(SwaggerConfiguration.class);
	
	@Value("${swagger.path}")
	private String swaggerPath;
	
	@Bean
	public Swagger getSwagger() {

//		String filename = "/home/jeavio64/Downloads/swagger-update-qa.json";
		
		Swagger swagger = swaggerService.parse(swaggerPath);

		log.info("Swagger File Source : " + swaggerPath);

		return swagger;
}

