package org.unrecoverable.spotifydjbot.admin.web;


import org.unrecoverable.spotifydjbot.web.BaseWebSocketSession;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class WebSession extends BaseWebSocketSession {

	private String username;
}
