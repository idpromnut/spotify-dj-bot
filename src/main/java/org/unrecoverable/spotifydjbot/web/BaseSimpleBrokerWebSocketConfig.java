package org.unrecoverable.spotifydjbot.web;

import java.util.Collection;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Base configuration class used for web socket applications.
 *
 */
@ComponentScan({"org.unrecoverable.webchat.web","org.unrecoverable.webchat.web.support"})
public abstract class BaseSimpleBrokerWebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {
	
//	@Autowired
//	private ApplicationEventPublisher eventPublisher;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker(getSimpleBrokerDestinations());
		registry.setApplicationDestinationPrefixes(getApplicationPrefixes());
	}

	@Override
	public void configureStompEndpoints(StompEndpointRegistry registry) {
		for(String endpoint: getEndpoints()) {
			registry.addEndpoint(endpoint).withSockJS();
		}
	}
	
//	@Bean
//	public WebSocketConnectHandlerDecoratorFactory wsConnectHandlerDecoratorFactory() {
//		return new WebSocketConnectHandlerDecoratorFactory(this.eventPublisher);
//	}
	
	protected abstract String[] getSimpleBrokerDestinations();
	
	protected abstract Collection<String> getEndpoints();
	
	protected abstract String[] getApplicationPrefixes();
}
