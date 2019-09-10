package org.jeavio.apigateway.config;

import org.apache.velocity.app.event.EventCartridge;
import org.jeavio.apigateway.EventHandler.VelocityInvalidReferenceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Used to create event cartridge with custom event handler
 * 
 * It's object can be used to bind with a velocity context
 * during template evaluation 
 */
@Configuration
public class VelocityEventCartridgeConfig {

	@Bean
	public EventCartridge getEventCartridge() {
		EventCartridge eventCartridge = new EventCartridge();
		eventCartridge.addInvalidReferenceEventHandler(new VelocityInvalidReferenceHandler());
		return eventCartridge;
	}

}
