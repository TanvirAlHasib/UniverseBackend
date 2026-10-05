package com.amr.uniVerse.housing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHousingCommentRequest(
        @NotBlank(message = "Comment cannot be blank")
        @Size(max = 2000, message = "Comment must not exceed 2000 characters")
        String content
) {}
