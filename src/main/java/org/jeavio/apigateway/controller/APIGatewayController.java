package org.jeavio.apigateway.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIGatewayController {
	
	@Autowired
	private Swagger swagger;
	
	
	@RequestMapping
	public Set<String> UrlMapper(HttpServletRequest request) {
		
		//URL Validation
		String requestHost=request.getHeader("host");
		String requestScheme=request.getScheme();
		return swagger.getPaths().keySet();
//		if(!(swaggerObject.getHost().equals(requestHost) && swaggerObject.getSchemes().contains(requestScheme))) {
//			HttpMethodService service= new HttpMethodService();
//        	service.set("402", "Forbidden error");
//        	return service;
//		}
		
		//URL parsing
//        String uri = request.getRequestURI();
//        String method = request.getMethod().toLowerCase();
//        HttpMethodService serviceBody=parseRequest(uri, method);
//        return serviceBody;
        
	}
	
	
	//To get Object of HttpMethodService using uri and request method
//	private HttpMethodService parseRequest(String uri,String method) {
//		Set<String> urlSet=swaggerObject.getPaths().keySet(); //Set of URis
//		
//		
//		//Check whether the request method is allowed
//        if(urlSet.contains(uri) && swaggerObject.getPaths().get(uri).get().keySet().contains(method)) {
//        	return swaggerObject.getPaths().get(uri).get(method);
//        }
//        else {
//			
//        	// Get List of UriTemplate objects
//			List<UriTemplate> templateList = new ArrayList<UriTemplate>();
//			for (String key : urlSet) {
//				UriTemplate uriTemplate = new UriTemplate(key);
//				templateList.add(uriTemplate);
//			}
//			
//			// Check
//			String matchedUri=null;
//			UriTemplate matchedTemplate=null;
//			for (UriTemplate urit : templateList) {
//				if (urit.matches(uri)) {	
//					matchedUri=urit.toString();
//					matchedTemplate=urit;
//					break;
//				}
//			}
//			
//			//URI Template Match
//			if(matchedUri!=null && swaggerObject.getPaths().get(matchedUri).get().keySet().contains(method)) {
//				Map<String,String> pathParams=matchedTemplate.match(matchedUri);
//				for(String param:pathParams.keySet())
//					request.setAttribute(param,pathParams.get(param));
//	        	return swaggerObject.getPaths().get(uri).get(method);
//	        }
//			else {
//	        	HttpMethodService service= new HttpMethodService();
//	        	service.set("402", "Forbidden error");
//	        	return service;
//			}
//        }
//	}
//	
//	//To get x-amazon-apigateway-integration Object from url & method
//	private static GatewayIntegration getIntegrationObject(String uri,String method)
//	{
//		Set<String> urlSet=swaggerObject.getPaths().keySet(); //Set of URLs
//		//Check whether the request method is allowed
//        if(urlSet.contains(uri) && swaggerObject.getPaths().get(uri).get().keySet().contains(method)) {
//        	return swaggerObject.getPaths().get(uri).get(method).getApigatewayIntegration();
//        }
//        return null;
//	}
	
}
