package org.jeavio.apigateway.service;

import org.apache.velocity.Template;
import org.jeavio.apigateway.model.SwaggerTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class TemplateService {

	@Autowired
	URLMethodService urlMethodService;
	
	@Autowired
	SwaggerTemplates swaggerTemplates;
	
	public Template getRequiredTemplate(String uri,String method,String templateType) {
		UriTemplate uriTemplate=urlMethodService.getUriTemp(uri);
		if(templateType.equals("requestTemplate")) {
		Template t=swaggerTemplates.get(uriTemplate.toString(), method).getRequestTemplate();
		return t;
		}
		else
			return swaggerTemplates.get(uriTemplate.toString(),method).getResponseTemplate(templateType);
	}
}
