package org.unrecoverable.spotifydjbot.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component(value="sessionManager")
public class SessionManager<T extends BaseWebSocketSession> {

	@Autowired(required=false)
	@Setter
	private WebSocketSessionProvider<T> sessionProvider = null;
	
	@Getter
	private Map<String,T> sessions = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	public T addSession(final String sessionId) {
		T session;
		if (sessionProvider != null) {
			session = sessionProvider.createNewWebSocketSession(sessionId);
		}
		else {
			session = (T) new BaseWebSocketSession();
		}
		
		session.setId(sessionId);
		sessions.put(sessionId, session);
		return session;
	}
	
	public void removeSession(final String sessionId) {
		sessions.remove(sessionId);
	}
	
	public T getAndAddIfMissing(final String sessionId) {
		T session = getSession(sessionId);
		if (session == null) session = addSession(sessionId);
		return session;
	}

	public T getSession(final String id) {
		if (sessions.containsKey(id)) {
			return sessions.get(id);
		}
		return null;
	}

	public List<T> getSessionsByFilter(Function<T, Boolean> f) {
		List<T> foundSessions = new LinkedList<>();
		for(T session: sessions.values()) {
			if (f.apply(session)) foundSessions.add(session);
		}
		return foundSessions;
	}
}
