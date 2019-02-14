package org.jeavio.apigateway;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@JsonIgnoreProperties(ignoreUnknown=true)
public class UrlService {
	
	private Map<String, HttpMethodService> httpMethodServiceMap = new LinkedHashMap<String, HttpMethodService>();
	private List<Parameter> parameters;
	
	public void setGet(HttpMethodService object) {
		httpMethodServiceMap.put("get",object);
	}
	
	public void setPost(HttpMethodService object) {
		httpMethodServiceMap.put("post",object);
	}
	
	public void setPut(HttpMethodService object) {
		httpMethodServiceMap.put("put",object);
	}
	
	public void setPatch(HttpMethodService object) {
		httpMethodServiceMap.put("patch",object);
	}
	
	public void setDelete(HttpMethodService object) {
		httpMethodServiceMap.put("delete",object);
	}
	
	public void setHead(HttpMethodService object) {
		httpMethodServiceMap.put("head",object);
	}
	
	public void setOptions(HttpMethodService object) {
		httpMethodServiceMap.put("options",object);
	}
	
	public void setTrace(HttpMethodService object) {
		httpMethodServiceMap.put("trace",object);
	}
	
	public HttpMethodService get(String method) {
		return httpMethodServiceMap.get(method);
	}
	
	public Map<String, HttpMethodService> get(){
		return httpMethodServiceMap;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
