package org.unrecoverable.spotifydjbot.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		log.debug("httpsession created: {}", se);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		log.debug("httpsession destroyed: {}", se);
	}
}