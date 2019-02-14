package org.jeavio.apigateway.model;

import java.util.Map;

import org.apache.velocity.Template;

public class GetTemplate {
	//First map for mapping request method with template
	//If responseTemplate then code is also required
	Map<String,Template> requestTemplateObjects;
	Map<String,Map<String,Map<String,Template>>> responseTemplateObjects;

	public Map<String, Template> getRequestTemplateObjects() {
		return requestTemplateObjects;
	}
	
	public Template getRequestTemplate(String method) {
		return requestTemplateObjects.get(method);
	}

//	public Template getRequestTemplateObject(String method,String type) {
//		return requestTemplateObjects.get(method).get(type);
//	}
//	public void setTemplateObjects(Map<String, Map<String, Template>> templateObjects) {
//		this.requestTemplateObjects = templateObjects;
//	}
//	
	
	

}
