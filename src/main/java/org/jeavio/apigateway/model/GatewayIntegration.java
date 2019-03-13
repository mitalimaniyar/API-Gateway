package org.jeavio.apigateway.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;


@JsonIgnoreProperties(ignoreUnknown = true)
@Component

/*
 * Used to depict x-amazon-apigateway-integration of swagger file
 * 
 * Contains values that is used to reformat request and response
 * 
 * Hierarchy : swagger->paths->{url}->method->x-amazon-apigateway-integration
 */
@Getter
@Setter
public class GatewayIntegration {

	private String credentials;
	private Map<String, IntegrationResponse> responses = new LinkedHashMap<String, IntegrationResponse>();
	private Map<String, String> requestTemplates = new LinkedHashMap<String, String>();
	private Map<String, String> requestParameters = new LinkedHashMap<String, String>();
	private String uri;
	private String passthroughBehaviour;
	private String httpMethod;
	private String cacheNamespace;
	private List<String> cacheKeyParameters = new ArrayList<String>();
	private String type;

}
