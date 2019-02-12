package org.jeavio.apigateway;

import org.springframework.stereotype.Component;

@Component
public class GatewayAuth {
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "GatewayAuth [type=" + type + "]";
	}
	
}
