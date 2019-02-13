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
	final static ObjectMapper objectMapper = new ObjectMapper();
	
	public static void main(String[] args) {
		
		swaggerObject=parseJson("swagger-update-effective-staging.json");
		SpringApplication.run(ApiGatewayApplication.class, args);	
	}
	
	private static Swagger parseJson(String fileName)
	{
		Swagger swagger=null;
		try {

			swagger= objectMapper.readValue(new File(fileName),Swagger.class);

			//System.out.print(get.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return swagger;
	}
	
	
	@Bean
	public Swagger getSwagger() {
		return swaggerObject;
	}

}

