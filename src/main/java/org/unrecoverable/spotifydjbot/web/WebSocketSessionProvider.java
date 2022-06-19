package org.unrecoverable.spotifydjbot.web;

public interface WebSocketSessionProvider<T extends BaseWebSocketSession> {

	T createNewWebSocketSession(final String sessionId);
}
