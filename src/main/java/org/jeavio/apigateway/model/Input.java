package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONValue;

/*
 * Generally used to depict $input objct in Velocity Template
 */
@Component
public class Input {

	private Map<String, Object> properties = new LinkedHashMap<>();

	public static Logger log = LoggerFactory.getLogger(Input.class);

	/*
	 * Below methods are generally used in Velocity references in $input object
	 * 
	 * 1.path : Takes a JSONPath expression string (x) and returns an object
	 * representation of the result. 2.json : This function evaluates a JSONPath
	 * expression and returns the results as a JSON string.
	 * 
	 * path returns object i.e. value of a key as object & json returns jsonString
	 * i.e. with quotes,colon etc.
	 * 
	 * 3.params(x):return param of key=x 4.params:return all params i.e. map
	 */
	public Object path(String reference) {
		Object patht = null;
		try {
			patht = JsonPath.read(properties, reference);
		} catch (Exception e) {
			log.error("Exception occured in parsing json reference {}", reference);
			log.error("Error: ", e.getMessage());
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

	/*
	 * Below two methods are used to set values in Object which is going to be
	 * referred as $input in velocity reference or else treated as request payload
	 * as Map
	 * 
	 * 1.putAll method : same as putAll method of Map 2.putBody method : takes
	 * request/response body as string and convert it to map using Jsonpath function
	 * and put it in properties
	 */
	public void putAll(Map<String, String> map) {
		properties.putAll(map);
	}

	public void putBody(String body) {
		properties.putAll(JsonPath.parse(body).read("$"));
	}

}
