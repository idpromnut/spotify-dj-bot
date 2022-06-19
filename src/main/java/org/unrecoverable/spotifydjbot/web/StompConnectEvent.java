package org.unrecoverable.spotifydjbot.web;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StompConnectEvent implements ApplicationListener<SessionConnectEvent> {

	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		StompHeaderAccessor stompHeader = StompHeaderAccessor.wrap(event.getMessage());
		log.debug("Connection from {}: {}", stompHeader.getSessionId(), stompHeader);
	}
}
