package org.jeavio.apigateway;

import java.util.Map;

import org.springframework.stereotype.Component;

import java.util.List;

public class Swagger {
	private String swagger;
	private Map<String, String> info;
	private String host;
	private List<String> schemes;
	private Map<String, UrlService> paths;
	private Map<String, Object> definitions;
	
	public String getSwagger() {
		return swagger;
	}
	public void setSwagger(String swagger) {
		this.swagger = swagger;
	}
	public Map<String, String> getInfo() {
		return info;
	}
	public void setInfo(Map<String, String> info) {
		this.info = info;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public List<String> getSchemes() {
		return schemes;
	}
	public void setSchemes(List<String> schemes) {
		this.schemes = schemes;
	}
	public Map<String, UrlService> getPaths() {
		return paths;
	}
	public void setPaths(Map<String, UrlService> paths) {
		this.paths = paths;
	}
	public Map<String, Object> getDefinitions() {
		return definitions;
	}
	public void setDefinitions(Map<String, Object> definitions) {
		this.definitions = definitions;
	}
	

}
