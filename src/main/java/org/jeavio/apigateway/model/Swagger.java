package org.jeavio.apigateway.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * Core object
 * contains all urls,all paths,all schemes
 * contains definitions for processing request and response
 * 
 * Hierarchy : swagger
 */
@Getter
@Setter 
public class Swagger {

	private String swagger;
	private Map<String, String> info;
	private String host;
	private List<String> schemes;
	private Map<String, UrlPath> paths;
	private Map<String, Object> definitions;

}
