package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
 * used to redefine frontend response by using backend response
 * 
 * Hierarchy : swagger->paths->{url}->method->x-amazon-apigateway-integration->Responses->{response code}
 */
@Component
@Getter	
@Setter 
public class IntegrationResponse {
	private String statusCode;
	private Map<String, String> responseParameters = new LinkedHashMap<String, String>();
	private Map<String, String> responseTemplates = new LinkedHashMap<String, String>();
	
}
