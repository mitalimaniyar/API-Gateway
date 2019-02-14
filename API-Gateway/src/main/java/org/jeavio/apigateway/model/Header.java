package org.jeavio.apigateway.model;

import org.springframework.stereotype.Component;

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
