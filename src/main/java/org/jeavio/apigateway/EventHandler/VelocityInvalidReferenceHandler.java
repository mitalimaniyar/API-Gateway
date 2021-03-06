package org.jeavio.apigateway.EventHandler;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.extern.slf4j.Slf4j;

/*
 * Used as custom invalid reference event handler in velocity template parsing
 * 
 * InvalidReferenceException is thrown when a property has null value or a method is not defined
 * and it will be handled by this method in which instead of returning null as default value we can
 * define custom values ,here "" i.e. blank.
 * 
 */
//@Slf4j
public class VelocityInvalidReferenceHandler implements InvalidReferenceEventHandler {

	private static final Logger log = LoggerFactory.getLogger("API");	
	@Override
	public Object invalidGetMethod(Context context, String reference, Object object, String property, Info info) {
		// TODO Auto-generated method stub
		reportInvalidReference(reference, info);
		return "";
	}

	@Override
	public boolean invalidSetMethod(Context context, String leftreference, String rightreference, Info info) {
		// TODO Auto-generated method stub
		reportInvalidReference(leftreference, info);
		return false;
	}

	@Override
	public Object invalidMethod(Context context, String reference, Object object, String method, Info info) {
		// TODO Auto-generated method stub
		if (reference == null) {
			reportInvalidReference(object.getClass().getName() + "." + method, info);
		} else {
			reportInvalidReference(reference, info);
		}
		return "";
	}

	private void reportInvalidReference(String reference, Info info) {
		// TODO Auto-generated method stub
		log.debug("{} : {}", reference, info);

	}

}
