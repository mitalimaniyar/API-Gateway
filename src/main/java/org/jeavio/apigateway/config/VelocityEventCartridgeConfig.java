package org.jeavio.apigateway.config;

import org.apache.velocity.app.event.EventCartridge;
import org.jeavio.apigateway.EventHandler.VelocityInvalidReferenceHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VelocityEventCartridgeConfig {

	@Bean
	public EventCartridge getEventCartridge() {
		EventCartridge eventCartridge = new EventCartridge();
		eventCartridge.addInvalidReferenceEventHandler(new VelocityInvalidReferenceHandler());
		return eventCartridge;
	}

}