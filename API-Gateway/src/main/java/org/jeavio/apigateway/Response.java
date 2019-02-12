package org.jeavio.apigateway;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Response {
	private String description;
	private Map<String, Header> headers;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, Header> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, Header> headers) {
		this.headers = headers;
	}
	
	
}
