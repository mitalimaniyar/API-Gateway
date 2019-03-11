package org.jeavio.apigateway.model;

import org.springframework.stereotype.Component;

/*
 * Hierarchy : swagger->paths->{url}->method->responses->{response code}->headers->header
 */
@Component
public class Header {
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
