package org.jeavio.apigateway.service;


import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegrationService {
	
	@Autowired
	Swagger swagger;
	
	@Autowired
	URLMethodService urlMethodService;
	
	public GatewayIntegration getIntegrationObject(String uri,String method)
	{
		//		Check whether the request method is allowed
        if(! urlMethodService.parseRequest(uri, method).get().containsKey("402")) {
        	return swagger.getPaths().get(uri).get(method).getApigatewayIntegration();
        }
        return new GatewayIntegration();
	}

}
