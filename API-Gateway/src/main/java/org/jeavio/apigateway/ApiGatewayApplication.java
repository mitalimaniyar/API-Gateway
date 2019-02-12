package org.jeavio.apigateway;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ApiGatewayApplication {
	
	static Swagger swaggerObject;
	static ObjectMapper objectMapper = new ObjectMapper();
	
	
	public static void main(String[] args) {
		
		try {

			swaggerObject= objectMapper.readValue(new File("swagger-update-effective-staging.json"),Swagger.class);

			//System.out.print(get.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SpringApplication.run(ApiGatewayApplication.class, args);	
	}
	@Bean
	public Swagger getSwagger() {
		return swaggerObject;
	}

}

