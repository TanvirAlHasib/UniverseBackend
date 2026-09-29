package com.amr.uniVerse.studyroom.service;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.studyroom.dto.ChatMessageRequest;
import com.amr.uniVerse.studyroom.dto.ChatMessageResponse;
import com.amr.uniVerse.studyroom.entity.ChatMessage;
import com.amr.uniVerse.studyroom.entity.MembershipStatus;
import com.amr.uniVerse.studyroom.entity.RoomMembership;
import com.amr.uniVerse.studyroom.entity.StudyRoom;
import com.amr.uniVerse.studyroom.repository.ChatMessageRepository;
import com.amr.uniVerse.studyroom.repository.RoomMembershipRepository;
import com.amr.uniVerse.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final RoomMembershipRepository membershipRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, User sender) {
        StudyRoom room = studyRoomRepository.findById(request.roomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 🔒 Authorization: only APPROVED members can send messages
        RoomMembership membership = membershipRepository
                .findByRoomIdAndUserId(room.getId(), sender.getId())
                .orElseThrow(() -> new RuntimeException("You are not a member of this room"));

        if (membership.getStatus() != MembershipStatus.APPROVED) {
            throw new RuntimeException("Your membership is not approved");
        }

        ChatMessage message = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(request.content())
                .build();

        return ChatMessageResponse.fromEntity(chatMessageRepository.save(message));
    }

    public List<ChatMessageResponse> getRoomMessages(Long roomId, User currentUser) {
        // 🔒 Only APPROVED members can read the history
        RoomMembership membership = membershipRepository
                .findByRoomIdAndUserId(roomId, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("You are not a member of this room"));

        if (membership.getStatus() != MembershipStatus.APPROVED) {
            throw new RuntimeException("Your membership is not approved");
        }

        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(ChatMessageResponse::fromEntity)
                .toList();
    }
}