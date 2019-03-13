package org.jeavio.apigateway.service;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jeavio.apigateway.model.CustomHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class CustomRequestCreator {

	public CustomHttpRequest parseIncomingRequest(HttpServletRequest request,Map<String,String> allParams,String requestBody) {
		
		Map<String,String> headers=new LinkedHashMap<String, String>();
		Enumeration<String> headerNames=request.getHeaderNames();
		
		while(headerNames.hasMoreElements()) {
			String header=headerNames.nextElement();
			headers.put(header, request.getHeader(header));
		}
		
		CustomHttpRequest customHttpRequest=CustomHttpRequest.builder()
											.scheme(request.getScheme())
											.method(request.getMethod())
											.requestURI(request.getRequestURI())
											.requestURL(new String(request.getRequestURL()))
											.queryString(request.getQueryString())
											.requestHeaders(headers)
											.parameterMap(allParams)
											.requestBody(requestBody)
											.build();
		return customHttpRequest;
		
	}
}
