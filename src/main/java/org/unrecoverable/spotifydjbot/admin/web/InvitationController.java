package org.unrecoverable.spotifydjbot.admin.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.unrecoverable.spotifydjbot.Version;
import org.unrecoverable.spotifydjbot.web.SessionManager;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class InvitationController implements ApplicationListener<SessionSubscribeEvent> {

//	private static final String UPDATE_CHAT_MESSAGE_TOPIC_NAME = "/topic/chatMessage";
//	private static final String SET_AUTHOR_MESSAGE_QUEUE_NAME = "/queue/setAuthor";

	@Autowired
	private SessionManager<WebSession> sessionManager;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	@RequestMapping(path={"/","/index.html"})
	public String index(Model model) {
	    model.addAttribute("appVersion", Version.getVersion());
	    return "index";
	}

//	@SendToUser(broadcast=false)
//	@MessageMapping("/loadMessagesFromServer")
//	public ChatMessage[] loadMessagesFromServer(SimpMessageHeaderAccessor simpHeaders) {
//		WebSession session = sessionManager.getSession(simpHeaders.getSessionId());
//		final String author = session.getAuthor();
//		return chatManager.getHistory().stream().filter( cm -> !(cm.getAuthor().equals( author ) && cm.getType().equals( ChatMessageType.JOIN.toString() ) ) ).toArray(ChatMessage[]::new);
//	}
//
//	@MessageMapping("/submitAuthorUpdate")
//	public void setAuthor(String author, SimpMessageHeaderAccessor simpHeaders) {
//		WebSession session = sessionManager.getSession(simpHeaders.getSessionId());
//		if (chatManager.authorChangedName( author, session.getAuthor() )) {
//			log.trace("Setting author {} on session {}", author, session.getId());
//			session.setAuthor( author );
//		}
//	}
//	
//	@MessageMapping("/submitNewChatMessage")
//	public void submitNewChatMessage(String newMessage, SimpMessageHeaderAccessor simpHeaders) {
//		WebSession session = sessionManager.getSession(simpHeaders.getSessionId());
//		log.debug("handling new message from user: {}", session.getAuthor());
//		chatManager.submit( session.getAuthor(), newMessage );
//	}
//
//	public void handleNewMessage(ChatMessage message) {
//		template.convertAndSend(UPDATE_CHAT_MESSAGE_TOPIC_NAME, message);
//		log.debug("forwarded chat message: {}", message);
//	}
	
	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor stompHeader = StompHeaderAccessor.wrap(event.getMessage());
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = stompHeader.getSessionId();
		WebSession session = sessionManager.getAndAddIfMissing( sessionId );
 	    StompCommand command = accessor.getCommand();

 	    if (command.equals(StompCommand.SUBSCRIBE)) {
	        String destination = accessor.getDestination();

//	        if (StringUtils.isNotBlank(destination) && destination.endsWith(SET_AUTHOR_MESSAGE_QUEUE_NAME)) {
//        		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
//					    .create(SimpMessageType.MESSAGE);
//				headerAccessor.setSessionId(sessionId);
//				headerAccessor.setLeaveMutable(true);
//		
//				template.convertAndSendToUser(sessionId, SET_AUTHOR_MESSAGE_QUEUE_NAME, session.getAuthor(), headerAccessor.getMessageHeaders());
//				log.debug("Pushed author update ({}) to Session {}", session.getAuthor(), sessionId);
//    			chatManager.userJoined( session.getAuthor() );
//            }
	    }
	}
}
