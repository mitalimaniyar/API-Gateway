package org.jeavio.apigateway.service;


import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class IntegrationService {
	
	@Autowired
	Swagger swagger;
	
	@Autowired
	URLMethodService urlMethodService;
	
	public GatewayIntegration getIntegrationObject(String uri,String method)
	{
		//		Check whether the request method is allowed
		UriTemplate matchedTemplate=null;
		try {
			matchedTemplate=urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(! urlMethodService.parseRequest(matchedTemplate.toString(), method).get().containsKey("402")) {
        	return swagger.getPaths().get(matchedTemplate.toString()).get(method).getApigatewayIntegration();
        }
        return new GatewayIntegration();
	}

}
