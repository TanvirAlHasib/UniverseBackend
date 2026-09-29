package com.amr.uniVerse.studyroom.dto;

import com.amr.uniVerse.studyroom.entity.RoomCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStudyRoomRequest(
        @NotBlank String title,
        String description,
        @NotNull RoomCategory category,
        Integer maxMembers
) {}