package org.jeavio.apigateway;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown=true)
public class HttpMethodService {

	private List<Parameter> parameters = new ArrayList<Parameter>();
	private GatewayAuth apigatewayAuth;
	private GatewayIntegration apigatewayIntegration;

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
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
	
	

	@Override
	public String toString() {
		return "HttpMethodService [parameters=" + parameters + ", apigatewayAuth=" + apigatewayAuth
				+ ", apigatewayIntegration=" + apigatewayIntegration + "]";
	}

}
