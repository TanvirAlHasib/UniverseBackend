package com.amr.uniVerse.housing.dto;

import com.amr.uniVerse.housing.entity.GenderPreference;
import com.amr.uniVerse.housing.entity.ListingType;
import com.amr.uniVerse.housing.entity.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateHousingListingRequest(
        @NotNull ListingType type,
        @NotBlank String title,
        String description,
        String location,
        RoomType roomType,
        GenderPreference genderPreference,
        Double price,       // for OFFERING
        Double budgetMin,   // for WANTED
        Double budgetMax,   // for WANTED
        LocalDate availableFrom,
        String imageUrl,
        String amenities
) {}