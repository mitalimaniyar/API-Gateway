package org.jeavio.apigateway.model;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

//@Builder
//@Getter
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


	public String getScheme() {
		return scheme;
	}


	public void setScheme(String scheme) {
		this.scheme = scheme;
	}


	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getRequestURI() {
		return requestURI;
	}


	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}


	public String getQueryString() {
		return queryString;
	}


	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}


	public String getRequestURL() {
		return requestURL;
	}


	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}


	public Map<String, Object> getAttributes() {
		return attributes;
	}


	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}


	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	public int getContentLength() {
		return contentLength;
	}


	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}


	public String getRequestBody() {
		return requestBody;
	}


	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}


	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}


	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}


	public Map<String, String> getParameterMap() {
		return parameterMap;
	}


	public void setParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
    
    
}
    
