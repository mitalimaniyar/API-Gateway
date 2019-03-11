package org.jeavio.apigateway.model;

import org.springframework.stereotype.Component;

/*
 * Used to depict x-amazon-apigateway-auth of swagger file
 * 
 * Hierarchy : swagger->paths->{url}->method->x-amazon-apigateway-auth
 */
@Component
public class GatewayAuth {

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
