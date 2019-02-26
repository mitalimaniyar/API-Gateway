package org.jeavio.apigateway.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jeavio.apigateway.model.IntegrationResponse;
import org.jeavio.apigateway.model.Swagger;
import org.jeavio.apigateway.service.IntegrationService;
import org.jeavio.apigateway.service.URLMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

				IntegrationResponse response;
				response = integrationService.getIntegrationObject(uri, method).getResponses().get(status.toString());
				if (response == null) {
					response=integrationService.getIntegrationObject(uri, method).getResponses().get("default");
					String expectedstatus = response.getStatusCode();
					
					if(!expectedstatus.equals(status.toString()))
						throw new ClientProtocolException("Unexpected response status: " + status);
					
					
				}
				if (status >= 200 && status < 300) {
					HttpEntity entity = myresponse.getEntity();

					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}
		};
		return responseHandler;
	}
}
