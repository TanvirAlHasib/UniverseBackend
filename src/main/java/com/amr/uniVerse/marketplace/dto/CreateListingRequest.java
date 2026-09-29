package com.amr.uniVerse.marketplace.dto;

import com.amr.uniVerse.marketplace.entity.Category;
import com.amr.uniVerse.marketplace.entity.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateListingRequest(
        @NotBlank String title,
        String description,
        @NotNull @Positive Double price,
        @NotNull Category category,
        @NotNull ItemCondition condition,
        String location,
        String imageUrl
) {}