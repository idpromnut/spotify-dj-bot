package org.unrecoverable.spotifydjbot.web;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketConnectHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

	private ApplicationEventPublisher eventPublisher;

	public WebSocketConnectHandlerDecoratorFactory(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		log.debug("WebSocketConnectHandlerDecoratorFactory: created with event publisher {}", this.eventPublisher);
	}

	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
		return new SessionWebSocketHandler(handler);
	}

	private final class SessionWebSocketHandler extends WebSocketHandlerDecorator {

		public SessionWebSocketHandler(WebSocketHandler delegate) {
			super(delegate);
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			log.debug("SessionWebSocketHandler.afterConnectionEstablished(): {}", session);
			super.afterConnectionEstablished(session);
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
			log.debug("SessionWebSocketHandler.afterConnectionClosed(): {}/{}", session, closeStatus);
			super.afterConnectionClosed(session, closeStatus);
		}
		
	}
}
