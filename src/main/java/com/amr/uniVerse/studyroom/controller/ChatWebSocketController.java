package com.amr.uniVerse.studyroom.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.studyroom.dto.ChatMessageRequest;
import com.amr.uniVerse.studyroom.dto.ChatMessageResponse;
import com.amr.uniVerse.studyroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Frontend sends a message to:  /app/chat.send
     * Subscribers of /topic/room/{roomId} receive it.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Unauthenticated");
        }

        // Extract our User entity from the Principal
        User sender = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        ChatMessageResponse response = chatService.saveMessage(request, sender);

        // Broadcast to all subscribers of this room
        messagingTemplate.convertAndSend("/topic/room/" + request.roomId(), response);
    }
}