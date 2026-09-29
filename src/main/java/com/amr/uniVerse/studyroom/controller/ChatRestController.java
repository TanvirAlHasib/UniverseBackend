package com.amr.uniVerse.studyroom.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.studyroom.dto.ChatMessageResponse;
import com.amr.uniVerse.studyroom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/study-rooms")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(chatService.getRoomMessages(roomId, currentUser));
    }
}