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
		UriTemplate uriTemplate=null;
		try {
			uriTemplate = urlMethodService.getUriTemp(uri,method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(templateType.equals("requestTemplate")) {
		Template t=swaggerTemplates.get(uriTemplate.toString(), method).getRequestTemplate();
		return t;
		}
		else {
			Template t=new Template();
			return t;
		}
	}
	
	
	public Template getRequiredTemplate(String uri,String method,String templateType,String responseCode) {
		UriTemplate uriTemplate=null;
		try {
			uriTemplate = urlMethodService.getUriTemp(uri,method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(templateType.equals("responseTemplate")) {
		Template t=swaggerTemplates.get(uriTemplate.toString(), method).getResponseTemplate(responseCode);
		return t;
		}
		else {
			Template t=new Template();
			return t;
		}
	}
}
