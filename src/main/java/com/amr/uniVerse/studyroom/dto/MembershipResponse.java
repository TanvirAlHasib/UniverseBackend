package com.amr.uniVerse.studyroom.dto;

import com.amr.uniVerse.studyroom.entity.MembershipStatus;
import com.amr.uniVerse.studyroom.entity.RoomMembership;

import java.time.LocalDateTime;

public record MembershipResponse(
        Long userId,
        String userName,
        String userEmail,
        MembershipStatus status,
        LocalDateTime requestedAt
) {
    public static MembershipResponse fromEntity(RoomMembership m) {
        return new MembershipResponse(
                m.getUser().getId(),
                m.getUser().getFirstName() + " " + m.getUser().getLastName(),
                m.getUser().getEmail(),
                m.getStatus(),
                m.getRequestedAt()
        );
    }
}