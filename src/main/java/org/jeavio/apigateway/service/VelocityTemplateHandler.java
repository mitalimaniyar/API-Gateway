package org.jeavio.apigateway.service;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.jeavio.apigateway.model.GatewayContext;
import org.jeavio.apigateway.model.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/*
 * Used for processing of velocity template
 */
@Service
@Slf4j
public class VelocityTemplateHandler {
	
	@Autowired
	EventCartridge eventCartridge;
	
	private VelocityEngine velocityEngine = new VelocityEngine();
	
	public String processTemplate(String uri,String method,String template,Input inputRequest,GatewayContext contextRequest) {
		
		VelocityContext context = new VelocityContext();
		
		context.put("context", contextRequest);
		context.put("input", inputRequest);
		
		eventCartridge.attachToContext(context);
		
		StringWriter writer = new StringWriter();
		
		if (velocityEngine.evaluate(context, writer, "requestTemplate", template)) {

			log.debug("{} : {}  Template found for request body and successfully merged ", uri, method);
			log.debug("{} : {} RequestBody  :  {}", uri, method, writer.toString());

			return writer.toString();
		} else {

			log.debug("{} : {}  Template found and merge failed for requestbody..returning null", uri, method);
			return null;
		}
	}

}
