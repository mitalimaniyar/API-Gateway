package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONValue;

@Component
public class RequestResponse {

	private Map<String, Object> properties = new LinkedHashMap<>();
	
	
    @JsonAnySetter
    public void set(String fieldName, Object value){
        this.properties.put(fieldName, value);
    }
    
    public Object path(String reference) {
		Object patht=JsonPath.read(properties,reference);
		return patht;
	}
    
    @JsonAnyGetter
    public Object get(String key) {
    	if(properties.containsKey(key))
    		return properties.get(key);
    	else
    		return "";
    }
    
    public Object params(String key) {
    	if(properties.containsKey(key))
    		return properties.get(key);
    	else
    		return "";
    }
    
    public void putAll(Map<String, String> map) {
    	properties.putAll(map);
    }
    
    
	public String json(String reference) {
    	Object patht=JsonPath.read(properties,reference);
    	String content =JSONValue.toJSONString(patht);
    	content=content.replace("\"","\\\"" );
		return content;
    }
    
	public Map<String,Object> params(){
    	return properties;
    }
	
	public void putBody(String Body) {
		properties.putAll(JsonPath.parse(Body).read("$"));
	}
	
}
