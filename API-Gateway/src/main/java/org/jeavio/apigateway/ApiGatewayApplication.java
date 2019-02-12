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
			HttpMethodService get= objectMapper.readValue(new File("sample.json"),HttpMethodService.class);
			System.out.print(get);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

