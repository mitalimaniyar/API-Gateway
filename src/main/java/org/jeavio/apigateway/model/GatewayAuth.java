package org.jeavio.apigateway.model;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
 * Used to depict x-amazon-apigateway-auth of swagger file
 * 
 * Hierarchy : swagger->paths->{url}->method->x-amazon-apigateway-auth
 */
@Component
@Getter
@Setter
public class GatewayAuth {

	private String type;

}
