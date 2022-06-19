package org.unrecoverable.spotifydjbot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SessionController implements ApplicationListener<ApplicationEvent> {

	@Autowired
	private SessionManager<? extends BaseWebSocketSession> sessionManager;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		if (event instanceof SessionConnectEvent) {
			sessionConnect((SessionConnectEvent)event);
		}
		else if (event instanceof SessionDisconnectEvent) {
			sessionDisconnect((SessionDisconnectEvent)event);
		}
	}

	private void sessionConnect(SessionConnectEvent event) {
		Message<byte[]> message = event.getMessage();
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		if (command.equals(StompCommand.CONNECT)) {
			String sessionId = accessor.getSessionId();
			String host = accessor.getHost();
			sessionManager.getAndAddIfMissing(sessionId);
			log.info("new session for: {} from {}", sessionId, host);
		}
	}
	
	private void sessionDisconnect(SessionDisconnectEvent event) {
		String sessionId = event.getSessionId();
		CloseStatus status = event.getCloseStatus();
		sessionManager.removeSession(sessionId);
		log.debug("session removed for: {} with status {}({})", sessionId, getCloseReason(status), status.getCode());
	}
	
	private String getCloseReason(final CloseStatus status) {
		switch(status.getCode()) {
			case 1000: return "NORMAL";
			case 1001: return "GOING_AWAY";
			case 1002: return "PROTOCOL_ERROR";
			case 1003: return "NOT_ACCEPTABLE";
			case 1005: return "NO_STATUS_CODE";
			case 1006: return "NO_CLOSE_FRAME";
			case 1007: return "BAD_DATA";
			case 1008: return "POLICY_VIOLATION";
			case 1009: return "TOO_BIG_TO_PROCESS";
			case 1010: return "REQUIRED_EXTENSION";
			case 1011: return "SERVER_ERROR";
			case 1012: return "SERVICE_RESTARTED";
			case 1013: return "SERVICE_OVERLOAD";
			case 1015: return "TLS_HANDSHAKE_FAILURE";
			case 4500: return "SESSION_NOT_RELIABLE";
		}
		
		return "UNKNOWN";
	}
}
