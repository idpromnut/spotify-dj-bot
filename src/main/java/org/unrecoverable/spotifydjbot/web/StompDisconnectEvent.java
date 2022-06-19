package org.unrecoverable.spotifydjbot.web;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StompDisconnectEvent implements ApplicationListener<SessionDisconnectEvent> {

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor stompHeader = StompHeaderAccessor.wrap(event.getMessage());
		log.debug("Disconnected {}: {}", stompHeader.getSessionId(), stompHeader);
	}
}
