package org.jeavio.apigateway;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfiguration {
	
	@Autowired
	Swagger swagger;
	
	@Autowired
	IntegrationService integrationService;
	
	@Bean
	public Templates getTemplates() {
		Templates template=new Templates();
		BufferedWriter writer;
		String vtlTemplate;
		
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		
		Set<String> urlSet=swagger.getPaths().keySet();
		
		for(String url:urlSet)
		{
			Set<String> methodSet=swagger.getPaths().get(url).get().keySet();
			Map<String,Template> templateObject=new LinkedHashMap<String, Template>();
			for(String method:methodSet) {
				vtlTemplate=integrationService.getIntegrationObject(url,method).getRequestTemplates().get("application/json");
				if(vtlTemplate!=null && !(vtlTemplate.equals("__passthrough__"))) {
					
					try {
						writer = new BufferedWriter(new FileWriter("template.vm"));
						writer.write(vtlTemplate);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Template t = ve.getTemplate("template.vm");
					
					//Put in list
					templateObject.put("requestTemplates",t);
					
					Map<String,IntegrationResponse> resposeTemplateList=integrationService.getIntegrationObject(url,method).getResponses();
				}
			}
		}

		template.setRequestTemplate(swagger.getHost());
		return template;
	}
}
