package com.amr.uniVerse.studyroom.dto;

import com.amr.uniVerse.studyroom.entity.MembershipStatus;
import com.amr.uniVerse.studyroom.entity.RoomCategory;

import java.time.LocalDateTime;

public record StudyRoomResponse(
        Long id,
        String title,
        String description,
        RoomCategory category,
        Integer maxMembers,
        long currentMembers,
        String creatorName,
        LocalDateTime createdAt,
        boolean isMember,
        MembershipStatus membershipStatus
) {}