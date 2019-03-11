package org.jeavio.apigateway.model;

import java.util.Map;

import org.springframework.stereotype.Component;

/*
 * used to define types of response that are going to be sent to frontend
 * 
 * Hierarchy : swagger->paths->{url}->method->response
 */
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
