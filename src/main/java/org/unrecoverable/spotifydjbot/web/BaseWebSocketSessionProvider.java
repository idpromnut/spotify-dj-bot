package org.unrecoverable.spotifydjbot.web;

public class BaseWebSocketSessionProvider implements WebSocketSessionProvider<BaseWebSocketSession> {

	@Override
	public BaseWebSocketSession createNewWebSocketSession(String sessionId) {
		BaseWebSocketSession session = new BaseWebSocketSession();
		session.setId(sessionId);
		return session;
	}

}
