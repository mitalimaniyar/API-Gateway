package org.jeavio.apigateway;

import org.springframework.stereotype.Controller;

@Controller
public class Header {
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
