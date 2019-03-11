package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlPath {

	private Map<String, HttpMethod> httpMethodServiceMap = new LinkedHashMap<String, HttpMethod>();
	private List<Parameter> parameters;

	public void setGet(HttpMethod object) {
		httpMethodServiceMap.put("get", object);
	}

	public void setPost(HttpMethod object) {
		httpMethodServiceMap.put("post", object);
	}

	public void setPut(HttpMethod object) {
		httpMethodServiceMap.put("put", object);
	}

	public void setPatch(HttpMethod object) {
		httpMethodServiceMap.put("patch", object);
	}

	public void setDelete(HttpMethod object) {
		httpMethodServiceMap.put("delete", object);
	}

	public void setHead(HttpMethod object) {
		httpMethodServiceMap.put("head", object);
	}

	public void setOptions(HttpMethod object) {
		httpMethodServiceMap.put("options", object);
	}

	public void setTrace(HttpMethod object) {
		httpMethodServiceMap.put("trace", object);
	}

	public HttpMethod get(String method) {
		return httpMethodServiceMap.get(method);
	}

	public Map<String, HttpMethod> get() {
		return httpMethodServiceMap;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
