package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONValue;

@Component
public class RequestResponse {

	private Map<String, Object> properties = new LinkedHashMap<>();

//	Method for $input in VTL
	public Object path(String reference) {
		Object patht = JsonPath.read(properties, reference);
		return patht;
	}

	public String json(String reference) {
		Object patht = JsonPath.read(properties, reference);
		String content = JSONValue.toJSONString(patht);
		content = content.replace("\"", "\\\"");
		return content;
	}

	public Object params(String key) {
		if (properties.containsKey(key))
			return properties.get(key);
		else
			return "";
	}

	public Map<String, Object> params() {
		return properties;
	}

//	Method for setting $input
	public void putAll(Map<String, String> map) {
		properties.putAll(map);
	}

	public void putBody(String Body) {
		properties.putAll(JsonPath.parse(Body).read("$"));
	}

}
