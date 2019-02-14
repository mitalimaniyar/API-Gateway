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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ApiGatewayApplication {
	
	final static ObjectMapper objectMapper = new ObjectMapper();
	static Map<String,GetTemplate> retrieveTemplate;
	
	//Main
	public static void main(String[] args) {
		
//		swaggerObject=parseJson("swagger-update-effective-staging.json");
//		retrieveTemplate=generateTemplates();
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	

	@Bean
	public Swagger getSwagger() {
		Swagger swagger = parseJson("swagger-update-effective-staging.json");
//		generateTemplates(swagger);
		return swagger;
	}
	
	
	//Json Parsing before application starts
	private static Swagger parseJson(String fileName)
	{
		Swagger swagger=new Swagger();
		try {

			swagger= objectMapper.readValue(new File(fileName),Swagger.class);
			
			//System.out.print(get.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return swagger;
	}
	
	
	//Template Objects Generation
	private static Map<String, GetTemplate> generateTemplates(Swagger swaggerObject) {
		
		BufferedWriter writer;
		String vtlTemplate;
		
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		
		Set<String> urlSet=swaggerObject.getPaths().keySet();
		
		for(String url:urlSet)
		{
			Set<String> methodSet=swaggerObject.getPaths().get(url).get().keySet();
			Map<String,Template> templateObject=new LinkedHashMap<String, Template>();
			for(String method:methodSet) {
				vtlTemplate=swaggerObject.getPaths().get(url).get(method).getApigatewayIntegration().getRequestTemplates().get("application/json");
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
					
					Map<String,IntegrationResponse> resposeTemplateList=swaggerObject.getPaths().get(url).get(method).getApigatewayIntegration().getResponses();
				}
			}
		}
		return null;
	}
	
}

