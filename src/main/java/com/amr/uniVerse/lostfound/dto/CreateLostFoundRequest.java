package com.amr.uniVerse.lostfound.dto;

import com.amr.uniVerse.lostfound.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateLostFoundRequest(
        @NotBlank String title,
        String description,
        @NotNull ItemType type,
        String location,
        @NotNull LocalDate incidentDate,
        String imageUrl
) {}