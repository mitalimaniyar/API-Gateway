package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;
import lombok.Setter;

/*
 * The object required to process by using url and method
 * 
 * Hierarchy : swagger->paths->{url}->method
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Component

public class HttpMethod {

	@Getter	@Setter private List<Parameter> parameters;
	@Getter	@Setter private Map<String, Response> responses;
	private GatewayAuth apigatewayAuth;
	private GatewayIntegration apigatewayIntegration;
	private Map<String, Object> extraTuples = new LinkedHashMap<String, Object>();
	
	@JsonGetter("x-amazon-apigateway-auth")
	public GatewayAuth getApigatewayAuth() {
		return apigatewayAuth;
	}

	@JsonSetter("x-amazon-apigateway-auth")
	public void setApigatewayAuth(GatewayAuth apigatewayAuth) {
		this.apigatewayAuth = apigatewayAuth;
	}

	@JsonGetter("x-amazon-apigateway-integration")
	public GatewayIntegration getApigatewayIntegration() {
		return apigatewayIntegration;
	}

	@JsonSetter("x-amazon-apigateway-integration")
	public void setApigatewayIntegration(GatewayIntegration apigatewayIntegration) {
		this.apigatewayIntegration = apigatewayIntegration;
	}

	@JsonAnySetter
	public void set(String key, Object value) {

		this.extraTuples.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, Object> get() {

		return extraTuples;
	}

}
