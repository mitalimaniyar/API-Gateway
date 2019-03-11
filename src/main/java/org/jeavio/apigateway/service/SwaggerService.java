package org.jeavio.apigateway.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SwaggerService {

	@Autowired
	ObjectMapper objectMapper;

	Swagger swagger;

	public static Logger log = LoggerFactory.getLogger(SwaggerService.class);

	public Swagger parse(String swaggerPath) {
		swagger = new Swagger();
		try {

			swagger = objectMapper.readValue(new File(swaggerPath), Swagger.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return swagger;
	}

	public GatewayIntegration getGatewayIntegration(String uri, String method) {
		// Check whether the request method is allowed
		UriTemplate matchedTemplate = getUriTemplate(uri, method);
		
		return swagger.getPaths().get(matchedTemplate.toString()).get(method).getApigatewayIntegration();

	}

	public UriTemplate getUriTemplate(String uri, String method) {
		Set<String> urlSet = swagger.getPaths().keySet(); // Set of URis

//		//Check whether the request method is allowed
		if (urlSet.contains(uri) && swagger.getPaths().get(uri).get().keySet().contains(method)) {

			log.debug("{} : {} Matched URI: {}", method, uri, uri);
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
				log.debug("{} : {}  Matched URI: ", method, uri, matchedTemplate.toString());
				return matchedTemplate;
			} else {
				log.error("No match found for required url and method pair");
				return null;
			}
		}
	}
}
