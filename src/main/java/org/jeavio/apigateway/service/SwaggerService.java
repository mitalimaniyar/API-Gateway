package org.jeavio.apigateway.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/*
 * Provides an interface that is used in communication with swagger object for
 * request/response processing
 * 
 * 1.parse : used to parse swagger.json file to swagger object
 * 2.getGatewayIntegration : used to return x-amazon-apigateway-integration
 *                           object correspond to a uri and method
 * 3.getUriTemplate : used to get UriTemplate object from request uri and method
 * 
 */
@Service
@Slf4j
public class SwaggerService {

	@Autowired
	ObjectMapper objectMapper;

	Swagger swagger;

	public Swagger parse(String swaggerPath) {
		swagger = new Swagger();
		try {
			File swaggerFile=new File(swaggerPath);
			swagger = objectMapper.readValue(swaggerFile, Swagger.class);

		} catch (Exception e) {
			log.error("Error starting application");
			log.error("Error : "+e);
			System.exit(0);
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

//			 //Check matching template
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
