package org.jeavio.apigateway.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomResponseHandler {

	@Autowired
	HttpServletRequest request;

	@Bean
	public ResponseHandler<String> getResponseHandler() {
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			@Override
			public String handleResponse(final HttpResponse myresponse) throws ClientProtocolException, IOException {
				int status = myresponse.getStatusLine().getStatusCode();
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
