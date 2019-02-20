package org.jeavio.apigateway.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.jayway.jsonpath.JsonPath;

@Component
public class InputRequest {

	private Map<String, Object> properties = new LinkedHashMap<>();
	
    @JsonAnySetter
    public void set(String fieldName, Object value){
        this.properties.put(fieldName, value);
    }
    
    public Object path(String reference) {
		Object patht=JsonPath.read(properties,reference);
		return patht;
	}
    
    public Object params(String key) {
    	return properties.get(key);
    }
    
    public void putAll(Map<String, String> map) {
    	properties.putAll(map);
    }
    
    public Object json(String reference) {
    	Object patht=JsonPath.read(properties,reference);
		return patht;
    }
    
    public Map<String,Object> params(){
    	return properties;
    }
}
