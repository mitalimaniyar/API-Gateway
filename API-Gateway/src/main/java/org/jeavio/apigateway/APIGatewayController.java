package org.jeavio.apigateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIGatewayController {
	
	@RequestMapping(value="*")
	public String Hello() {
		return "Hello World";
	}
}
