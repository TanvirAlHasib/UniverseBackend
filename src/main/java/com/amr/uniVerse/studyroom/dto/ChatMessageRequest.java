package com.amr.uniVerse.studyroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull Long roomId,
        @NotBlank String content
) {}