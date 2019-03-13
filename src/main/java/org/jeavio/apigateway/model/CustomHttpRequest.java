package org.jeavio.apigateway.model;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomHttpRequest {
    
    private String scheme;
    private String method;
    private String requestURI;
    private String queryString;
    private String requestURL;
    @Builder.Default private Map<String,Object> attributes=new LinkedHashMap<String, Object>();
    private String contentType;
    private int contentLength;
    private String requestBody;
    private Map<String,String> requestHeaders;
    private Map<String,String> parameterMap;
    private String protocol;

	public Enumeration<String> getAttributeNames() {
    	return Collections.enumeration(attributes.keySet());
    }
	
    
    public String getAttribute(String name) {
    	return (String) attributes.get(name);
    }
    
    public String getHeader(String name) {
    	return (String) requestHeaders.get(name);
    }
    
    public int getIntHeader(String name) {
    	int value=Integer.parseInt((String) requestHeaders.get(name));
    	return value;
    }
    
    public Enumeration<String> getHeaderNames(){
    	return Collections.enumeration(requestHeaders.keySet());
    }
    
    public String getParameter(String name) {
    	return parameterMap.get(name);
    }
    
    public Enumeration<String> getParameterNames(){
    	return Collections.enumeration(parameterMap.keySet());
    }
    
    public String getParameterValues(String name) {
    	return parameterMap.get(name);
    }
    
    public void setAttribute(String name,Object value) {
    	attributes.put(name, value);
    }
    
}
    
