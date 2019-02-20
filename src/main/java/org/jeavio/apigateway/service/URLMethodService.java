package org.jeavio.apigateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jeavio.apigateway.model.HttpMethodObject;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class URLMethodService {
	
	@Autowired
	Swagger swagger;
	
	public HttpMethodObject parseRequest(String uri,String method) {
		
		UriTemplate matchedTemplate=null;
		try {
			matchedTemplate=getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(matchedTemplate!=null)
			return swagger.getPaths().get(matchedTemplate.toString()).get(method);
		else {
			HttpMethodObject object=new HttpMethodObject();
			object.set("402","Forbidden error-Method not allowed");
			return object;
		}

	}
	
	
	public UriTemplate getUriTemp(String uri,String method) throws Exception {
		Set<String> urlSet=swagger.getPaths().keySet(); //Set of URis
		
		
//		//Check whether the request method is allowed
        if(urlSet.contains(uri) && swagger.getPaths().get(uri).get().keySet().contains(method)){
        	return new UriTemplate(uri);
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
			UriTemplate matchedTemplate=null;
			for (UriTemplate urit : templateList) {
				if (urit.matches(uri)) {	
					matchedTemplate=urit;
					break;
				}
			}
			if(matchedTemplate!=null && swagger.getPaths().get(matchedTemplate.toString()).get().keySet().contains(method)) {
	        	return matchedTemplate;
			}
			else {
				throw new Exception("No match Found Exception");
			}
	    }
	}

}