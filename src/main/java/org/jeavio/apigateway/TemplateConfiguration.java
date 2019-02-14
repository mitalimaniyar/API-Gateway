package org.jeavio.apigateway;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.model.TemplateObjects;
import org.jeavio.apigateway.model.SwaggerTemplates;
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
	public SwaggerTemplates getSwaggerTemplates() {
		SwaggerTemplates swaggerTemplates = new SwaggerTemplates();

		BufferedWriter writer;
		String vtlTemplate;

		VelocityEngine velocityEngine=new VelocityEngine();
		velocityEngine.init();

		Set<String> urlSet = swagger.getPaths().keySet();
		Map<String, Map<String, TemplateObjects>> urlTemplates = new LinkedHashMap<String, Map<String, TemplateObjects>>();

		for (String url : urlSet) {
			Set<String> methodSet = swagger.getPaths().get(url).get().keySet();
			Map<String, TemplateObjects> methodTemplates = new LinkedHashMap<String, TemplateObjects>();

			for (String method : methodSet) {

				TemplateObjects templateObjects = new TemplateObjects();

//				//RequestTemplate Processing
				vtlTemplate = integrationService.getIntegrationObject(url, method).getRequestTemplates()
						.get("application/json");
				if (vtlTemplate != null && !(vtlTemplate.equals("__passthrough__"))) {

					try {
						writer = new BufferedWriter(new FileWriter("template.vm"));
						writer.write(vtlTemplate);
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Template template = velocityEngine.getTemplate("template.vm");
					new File("template.vm").delete();

					templateObjects.setRequestTemplate(template);

				}

//				//Response Templates Processing
				Map<String, IntegrationResponse> resposeTemplateList = integrationService
						.getIntegrationObject(url, method).getResponses();
				Map<String, Template> responseTemplates = new LinkedHashMap<String, Template>();

				for (String responseCode : resposeTemplateList.keySet()) {
					vtlTemplate = resposeTemplateList.get(responseCode).getResponseTemplates().get("application/json");
					if (vtlTemplate != null && !(vtlTemplate.equals("__passthrough__"))) {

						try {
							writer = new BufferedWriter(new FileWriter("template.vm"));
							writer.write(vtlTemplate);
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Template template = velocityEngine.getTemplate("template.vm");
						new File("template.vm").delete();

						responseTemplates.put(responseCode, template);
					}

				}

				templateObjects.setResponseTemplate(responseTemplates);
				methodTemplates.put(method, templateObjects);
			}

			urlTemplates.put(url, methodTemplates);
		}

		swaggerTemplates.setTemplates(urlTemplates);
		return swaggerTemplates;
	}
}
