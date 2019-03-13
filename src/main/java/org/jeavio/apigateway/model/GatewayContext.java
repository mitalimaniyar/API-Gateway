package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
 * Generally Used to depict $context object in Velocity Template
 */
@Component
@Getter
@Setter
public class GatewayContext {

	private Map<String, String> identity = new LinkedHashMap<String, String>();
	private String httpMethod;
	private String protocol;

}
