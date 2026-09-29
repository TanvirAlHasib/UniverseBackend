package com.amr.uniVerse.studyroom.dto;

import com.amr.uniVerse.studyroom.entity.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long roomId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime sentAt
) {
    public static ChatMessageResponse fromEntity(ChatMessage msg) {
        return new ChatMessageResponse(
                msg.getId(),
                msg.getRoom().getId(),
                msg.getSender().getId(),
                msg.getSender().getFirstName() + " " + msg.getSender().getLastName(),
                msg.getContent(),
                msg.getSentAt()
        );
    }
}