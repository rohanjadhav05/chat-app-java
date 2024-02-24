package com.websocket.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.websocket.chat.controller.ChatMessage;
import com.websocket.chat.controller.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEvenetListener {
	
	@Autowired
	private final SimpMessageSendingOperations messageTemplate;
	
	@EventListener
	public void handleWebSocketDisconnectListner(SessionDisconnectEvent event) {
		// Event of disconnected User
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String)headerAccessor.getSessionAttributes().get("username");
		if(username  != null ){
			log.info("User disconnected : { "+username+" }");
			var chatMessage = ChatMessage.builder()
									.type(MessageType.LEAVE)
									.sender(username)
									.build();
			
			// which will tell all peoples in chat that particular username has left the chat.
			messageTemplate.convertAndSend("/topic/public", chatMessage);
		}
	}
}
