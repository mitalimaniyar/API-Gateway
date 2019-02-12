package org.jeavio.apigateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class IntegrationResponse {
	private String statusCode;
	private Map<String,String> responseParameters=new LinkedHashMap<String,String>();
	private Map<String,String> responseTemplates=new LinkedHashMap<String, String>();
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Map<String, String> getResponseParameters() {
		return responseParameters;
	}
	public void setResponseParameters(Map<String, String> responseParamters) {
		this.responseParameters = responseParamters;
	}
	@Override
	public String toString() {
		return "IntegrationResponse [statusCode=" + statusCode + ", responseParameters=" + responseParameters
				+ ", responseTemplates=" + responseTemplates + "]";
	}
	public Map<String, String> getResponseTemplates() {
		return responseTemplates;
	}
	public void setResponseTemplates(Map<String, String> responseTemplates) {
		this.responseTemplates = responseTemplates;
	}
	
	
}
