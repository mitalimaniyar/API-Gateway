package org.jeavio.apigateway.model;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
 *used to define parameter
 * 
 * Hierarchy : swagger->paths->{url}->method->parameters
 * 				OR
 * Hierarchy : swagger->paths->{url}->parameters
 */
@Component
@Getter	
@Setter 
public class Parameter {

	private String name;
	private String in;
	private String description;
	private boolean required;
	private String type;
	private String format;

}
