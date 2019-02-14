package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@JsonIgnoreProperties(ignoreUnknown=true)
public class UrlObject {
	
	private Map<String, HttpMethodObject> httpMethodServiceMap = new LinkedHashMap<String, HttpMethodObject>();
	private List<Parameter> parameters;
	
	public void setGet(HttpMethodObject object) {
		httpMethodServiceMap.put("get",object);
	}
	
	public void setPost(HttpMethodObject object) {
		httpMethodServiceMap.put("post",object);
	}
	
	public void setPut(HttpMethodObject object) {
		httpMethodServiceMap.put("put",object);
	}
	
	public void setPatch(HttpMethodObject object) {
		httpMethodServiceMap.put("patch",object);
	}
	
	public void setDelete(HttpMethodObject object) {
		httpMethodServiceMap.put("delete",object);
	}
	
	public void setHead(HttpMethodObject object) {
		httpMethodServiceMap.put("head",object);
	}
	
	public void setOptions(HttpMethodObject object) {
		httpMethodServiceMap.put("options",object);
	}
	
	public void setTrace(HttpMethodObject object) {
		httpMethodServiceMap.put("trace",object);
	}
	
	public HttpMethodObject get(String method) {
		return httpMethodServiceMap.get(method);
	}
	
	public Map<String, HttpMethodObject> get(){
		return httpMethodServiceMap;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
