package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import net.minidev.json.JSONValue;

@Component
public class RequestResponse {

	private Map<String, Object> properties = new LinkedHashMap<>();
	
	public static Logger log=LoggerFactory.getLogger(RequestResponse.class);

//	Method for $input in VTL
	public Object path(String reference) {
		Object patht = null;
		try{
			patht=JsonPath.read(properties, reference);
		}catch(Exception e) {
			log.error("Exception occured in parsing json reference {}",reference);
			log.error("Error: ", e);
			return "";
		}
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
