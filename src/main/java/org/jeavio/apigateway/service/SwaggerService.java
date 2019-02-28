package org.jeavio.apigateway.service;

import java.io.File;
import java.io.IOException;

import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SwaggerService {

	@Autowired
	ObjectMapper objectMapper;

	public Swagger parse(String swaggerPath) {
		Swagger swagger = new Swagger();
		try {

			swagger = objectMapper.readValue(new File(swaggerPath), Swagger.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return swagger;
	}

}
