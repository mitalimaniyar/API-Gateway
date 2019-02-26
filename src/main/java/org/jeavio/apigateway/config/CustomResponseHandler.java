package org.jeavio.apigateway.config;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.RequestResponse;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.IntegrationService;
import org.jeavio.apigateway.service.URLMethodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class CustomResponseHandler {

	@Autowired
	HttpServletRequest request;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	URLMethodService urlMethodService;

	@Autowired
	Swagger swagger;

	@Bean
	public ResponseHandler<String> getResponseHandler() {
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			@Override
			public String handleResponse(final HttpResponse myresponse) throws ClientProtocolException, IOException {

				Integer status = myresponse.getStatusLine().getStatusCode();

				String uri = request.getRequestURI();
				String method = request.getMethod();
				
				UriTemplate uriTemplate = new UriTemplate("/");
				try {
					uriTemplate = urlMethodService.getUriTemp(uri, method);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				IntegrationResponse integratedResponse;
				integratedResponse = integrationService.getIntegrationObject(uriTemplate.toString(), method).getResponses().get(status.toString());
				if (integratedResponse == null) {
					integratedResponse=integrationService.getIntegrationObject(uriTemplate.toString(), method).getResponses().get("default");
					String expectedstatus = integratedResponse.getStatusCode();
					
					if(!expectedstatus.equals(status.toString()))
						throw new ClientProtocolException("Unexpected response status: " + status);
										
				}
				
				
				HttpEntity entity = myresponse.getEntity();
				if(entity==null)
					return null;
				
//				Parsing templates
				String template = null;
				if(integratedResponse.getResponseTemplates()!=null)
					template=integratedResponse.getResponseTemplates().get("application/json");
				
				if(template==null)
					return null;
				else if(template.equals("__passthrough__"))
					return EntityUtils.toString(entity);
				else {
					ObjectMapper objectMapper=new ObjectMapper();
					RequestResponse outputResponse=objectMapper.readValue(EntityUtils.toString(entity).getBytes(),RequestResponse.class);
					
					VelocityEngine velocityEngine=new VelocityEngine();
					VelocityContext context = new VelocityContext();
					
					context.put("input", outputResponse);
					StringWriter writer = new StringWriter();
					
					if (velocityEngine.evaluate(context, writer, "responseTemplate", template))
						return writer.toString();
					else
						return null;
				}
			}
		};
		return responseHandler;
	}
}
