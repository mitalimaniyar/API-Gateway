package org.jeavio.apigateway.model;


import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/*
 * Hierarchy : swagger->paths->{url}->method->responses->{response code}->headers->header
 */
@Component
@Getter
@Setter
public class Header {
	private String type;

}
