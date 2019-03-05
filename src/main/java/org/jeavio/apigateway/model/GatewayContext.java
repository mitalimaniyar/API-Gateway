package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class GatewayContext {

	private Map<String, String> identity = new LinkedHashMap<String, String>();
	private String httpMethod;
	private String protocol;
	
	public Map<String, String> getIdentity() {
		return identity;
	}

	public void setIdentity(Map<String, String> identity) {
		this.identity = identity;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
