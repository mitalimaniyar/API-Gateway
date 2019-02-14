package org.jeavio.apigateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeavio.apigateway.model.HttpMethodService;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class URLMethodService {
	
	@Autowired
	Swagger swagger;
	
	public HttpMethodService parseRequest(String uri,String method) {
		Set<String> urlSet=swagger.getPaths().keySet(); //Set of URis
		
		
//		//Check whether the request method is allowed
        if(urlSet.contains(uri) && swagger.getPaths().get(uri).get().keySet().contains(method)) {
        	return swagger.getPaths().get(uri).get(method);
        }
        else 
        {
			
//        	 //Get List of UriTemplate objects
			List<UriTemplate> templateList = new ArrayList<UriTemplate>();
			for (String key : urlSet) {
				UriTemplate uriTemplate = new UriTemplate(key);
				templateList.add(uriTemplate);
			}
			
//			 //Check
			String matchedUri=null;
			UriTemplate matchedTemplate=null;
			for (UriTemplate urit : templateList) {
				if (urit.matches(uri)) {	
					matchedUri=urit.toString();
					matchedTemplate=urit;
					break;
				}
			}
		
//			//URI Template Match
			if(matchedUri!=null && swagger.getPaths().get(matchedUri).get().keySet().contains(method)) {
				Map<String,String> pathParams=matchedTemplate.match(matchedUri);
				for(String param:pathParams.keySet()) {
//					request.setAttribute(param,pathParams.get(param));
					/*
					 * Code to add pathparam in a list
					 * yet to be decided how
					 * 
					 */
				}
	        	return swagger.getPaths().get(uri).get(method);
	        }
			else {
	        	HttpMethodService service= new HttpMethodService();
	        	service.set("402", "Forbidden error");
	        	return service;
			}
		}

	}	

}
