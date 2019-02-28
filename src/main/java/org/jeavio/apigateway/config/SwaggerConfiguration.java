package org.jeavio.apigateway.config;

import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.SwaggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

	@Autowired
	SwaggerService swaggerService;

	public static Logger APIGatewayLogger = LoggerFactory.getLogger(SwaggerConfiguration.class);

	@Bean
	public Swagger getSwagger() {

		String filename = "/home/jeavio64/Downloads/swagger-update-qa.json";

		Swagger swagger = swaggerService.parse(filename);

		APIGatewayLogger.info("Swagger File Source : " + filename);

		return swagger;
	}
}
