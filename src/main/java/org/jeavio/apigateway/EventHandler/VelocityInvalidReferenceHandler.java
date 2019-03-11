package org.jeavio.apigateway.EventHandler;

import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityInvalidReferenceHandler implements InvalidReferenceEventHandler {

	public static Logger log = LoggerFactory.getLogger(VelocityInvalidReferenceHandler.class);

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
