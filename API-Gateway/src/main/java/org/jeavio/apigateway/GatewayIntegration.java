package org.jeavio.apigateway;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Component
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
	


	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public Map<String, IntegrationResponse> getResponses() {
		return responses;
	}

	public void setResponses(Map<String, IntegrationResponse> responses) {
		this.responses = responses;
	}

	public Map<String, String> getRequestTemplates() {
		return requestTemplates;
	}

	public void setRequestTemplates(Map<String, String> requestTemplates) {
		this.requestTemplates = requestTemplates;
	}

	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPassthroughBehaviour() {
		return passthroughBehaviour;
	}

	public void setPassthroughBehaviour(String passthroughBehaviour) {
		this.passthroughBehaviour = passthroughBehaviour;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getCacheNamespace() {
		return cacheNamespace;
	}

	public void setCacheNamespace(String cacheNamespace) {
		this.cacheNamespace = cacheNamespace;
	}

	public List<String> getCacheKeyParameters() {
		return cacheKeyParameters;
	}

	public void setCacheKeyParameters(List<String> cacheKeyParameters) {
		this.cacheKeyParameters = cacheKeyParameters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	
	@Override
	public String toString() {
		return "GatewayIntegration [credentials=" + credentials + ", responses=" + responses + ", requestTemplates="
				+ requestTemplates + ", requestParameters=" + requestParameters + ", uri=" + uri
				+ ", passthroughBehaviour=" + passthroughBehaviour + ", httpMethod=" + httpMethod + ", cacheNamespace="
				+ cacheNamespace + ", cacheKeyParameters=" + cacheKeyParameters + ", type=" + type + "]";
	}


}
