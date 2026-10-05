package com.amr.uniVerse.studyroom.dto;

import com.amr.uniVerse.studyroom.entity.MembershipStatus;
import com.amr.uniVerse.studyroom.entity.RoomMembership;

import java.time.LocalDateTime;

public record MembershipResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        String departmentName,
        MembershipStatus status,
        LocalDateTime requestedAt
) {
    public static MembershipResponse fromEntity(RoomMembership m) {
        String deptName = (m.getUser() != null && m.getUser().getDepartment() != null)
                ? m.getUser().getDepartment().getName()
                : null;
        return new MembershipResponse(
                m.getId(),
                m.getUser() != null ? m.getUser().getId() : null,
                m.getUser() != null ? (m.getUser().getFirstName() + " " + m.getUser().getLastName()).trim() : "Unknown",
                m.getUser() != null ? m.getUser().getEmail() : "",
                deptName,
                m.getStatus(),
                m.getRequestedAt()
        );
    }
}