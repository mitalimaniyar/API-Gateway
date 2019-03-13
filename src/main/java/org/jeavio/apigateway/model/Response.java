package org.jeavio.apigateway.model;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
 * used to define types of response that are going to be sent to frontend
 * 
 * Hierarchy : swagger->paths->{url}->method->response
 */
@Component
@Getter	
@Setter 
public class Response {

	private String description;
	private Map<String, Header> headers;

}
