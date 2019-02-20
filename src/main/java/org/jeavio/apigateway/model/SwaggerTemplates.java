package org.jeavio.apigateway.model;

import java.util.Map;


public class SwaggerTemplates {
		Map<String,Map<String,TemplateObjects>> templates;

		public Map<String, Map<String, TemplateObjects>> getTemplates() {
			return templates;
		}

		public void setTemplates(Map<String, Map<String, TemplateObjects>> templates) {
			this.templates = templates;
		}
		
		public Map<String,TemplateObjects> get(String uri){
			return templates.get(uri);
		}
		
		public TemplateObjects get(String uri,String method) {
			return templates.get(uri).get(method);
		}
}
