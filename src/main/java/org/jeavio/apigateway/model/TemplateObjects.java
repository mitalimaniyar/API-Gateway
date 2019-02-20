package org.jeavio.apigateway.model;

import java.util.Map;

import org.apache.velocity.Template;
import org.springframework.stereotype.Component;

@Component
public class TemplateObjects {
	private Template requestTemplate;
	private Map<String,Template> responseTemplate;
	
	public Template getRequestTemplate() {
		return requestTemplate;
	}
	public void setRequestTemplate(Template requestTemplate) {
		this.requestTemplate = requestTemplate;
	}
	public Map<String, Template> getResponseTemplate() {
		return responseTemplate;
	}
	public void setResponseTemplate(Map<String, Template> responseTemplate) {
		this.responseTemplate = responseTemplate;
	}
	
	public Template getResponseTemplate(String responseCode) {
		return responseTemplate.get(responseCode);
	}

}
