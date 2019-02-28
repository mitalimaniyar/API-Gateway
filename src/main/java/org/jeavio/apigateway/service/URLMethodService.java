package org.jeavio.apigateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jeavio.apigateway.model.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class URLMethodService {

	@Autowired
	Swagger swagger;

	public static Logger APIGatewayLogger = LoggerFactory.getLogger(URLMethodService.class);

	public UriTemplate getUriTemp(String uri, String method) throws Exception {
		Set<String> urlSet = swagger.getPaths().keySet(); // Set of URis

//		//Check whether the request method is allowed
		if (urlSet.contains(uri) && swagger.getPaths().get(uri).get().keySet().contains(method)) {

			APIGatewayLogger.debug(method + " " + uri + " " + " \nMatched URI: " + uri);
			return new UriTemplate(uri);
		} else {
//        	 //Get List of UriTemplate objects
			List<UriTemplate> templateList = new ArrayList<UriTemplate>();
			for (String key : urlSet) {
				UriTemplate uriTemplate = new UriTemplate(key);
				templateList.add(uriTemplate);
			}

//			 //Check
			UriTemplate matchedTemplate = null;
			for (UriTemplate urit : templateList) {
				if (urit.matches(uri)) {

					matchedTemplate = urit;
					break;
				}
			}
			if (matchedTemplate != null
					&& swagger.getPaths().get(matchedTemplate.toString()).get().keySet().contains(method)) {
				APIGatewayLogger.debug(method + " " + uri + " " + " \nMatched URI: " + matchedTemplate.toString());
				return matchedTemplate;
			} else {
				APIGatewayLogger.debug(method + " " + uri + " "
						+ " \n No Matching URI Found or Method not allowed for that URI...throwing Exception ");
				throw new Exception("No match Found Exception");
			}
		}
	}

}