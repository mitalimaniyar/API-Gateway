package org.jeavio.apigateway.service;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeavio.apigateway.model.CustomHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class CustomRequestCreator {

	public CustomHttpRequest parseIncomingRequest(HttpServletRequest request, Map<String, String> allParams,
			String requestBody) {

		Map<String, String> headers = new LinkedHashMap<String, String>();
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();
			headers.put(header, request.getHeader(header));
		}

		CustomHttpRequest customHttpRequest = new CustomHttpRequest();

		customHttpRequest.setScheme(request.getScheme());
		customHttpRequest.setMethod(request.getMethod());
		customHttpRequest.setRequestURI(request.getRequestURI());
		customHttpRequest.setRequestURL(new String(request.getRequestURL()));
		customHttpRequest.setQueryString(request.getQueryString());
		customHttpRequest.setRequestHeaders(headers);
		customHttpRequest.setParameterMap(allParams);
		customHttpRequest.setRequestBody(requestBody);
//											.build();
		return customHttpRequest;

	}
}
