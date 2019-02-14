package org.jeavio.apigateway.service;

import java.util.Set;

import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegrationService {
	
	@Autowired
	Swagger swagger;
	
	public GatewayIntegration getIntegrationObject(String uri,String method)
	{
		Set<String> urlSet=swagger.getPaths().keySet(); //Set of URLs

		//		Check whether the request method is allowed
        if(urlSet.contains(uri) && swagger.getPaths().get(uri).get().keySet().contains(method)) {
        	return swagger.getPaths().get(uri).get(method).getApigatewayIntegration();
        }
        return new GatewayIntegration();
	}

}
