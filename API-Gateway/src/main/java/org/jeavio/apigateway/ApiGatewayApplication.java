package org.jeavio.apigateway;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ApiGatewayApplication {
	
	
	public static void main(String[] args) {
		
		SpringApplication.run(ApiGatewayApplication.class, args);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Swagger get= objectMapper.readValue(new File("swagger-update-effective-staging.json"),Swagger.class);
			//System.out.print(get.toString());
			System.out.println(get.getPaths().get("/api/comments/{commentId}/delete").get().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

