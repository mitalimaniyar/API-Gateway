package org.jeavio.apigateway;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class HttpMethodService {

	private List<Parameter> parameters;
	private Map<String, Response> responses;
	private GatewayAuth apigatewayAuth;
	private GatewayIntegration apigatewayIntegration;
	private Map<String,Object> extraTuples=new LinkedHashMap<String, Object>();

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public Map<String, Response> getResponses() {
		return responses;
	}

	public void setResponses(Map<String, Response> response) {
		this.responses = response;
	}

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
	public void set(String key,Object value) {
		this.extraTuples.put(key, value);
	}
	
	@JsonGetter
	public Map<String, Object> get(){
		return extraTuples;
	}

	@Override
	public String toString() {
		return "HttpMethodService [parameters=" + parameters + ", responses=" + responses + ", apigatewayAuth="
				+ apigatewayAuth + ", apigatewayIntegration=" + apigatewayIntegration + "]";
	}

	

}
