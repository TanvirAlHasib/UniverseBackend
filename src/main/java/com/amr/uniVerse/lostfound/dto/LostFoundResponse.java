package com.amr.uniVerse.lostfound.dto;

import com.amr.uniVerse.lostfound.entity.ItemStatus;
import com.amr.uniVerse.lostfound.entity.ItemType;
import com.amr.uniVerse.lostfound.entity.LostFoundItem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LostFoundResponse(
        Long id,
        String title,
        String description,
        ItemType type,
        ItemStatus status,
        String location,
        LocalDate incidentDate,
        String imageUrl,
        String reporterName,
        String reporterEmail,
        LocalDateTime createdAt
) {
    public static LostFoundResponse fromEntity(LostFoundItem item) {
        return new LostFoundResponse(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getType(),
                item.getStatus(),
                item.getLocation(),
                item.getIncidentDate(),
                item.getImageUrl(),
                item.getReporter().getFirstName() + " " + item.getReporter().getLastName(),
                item.getReporter().getEmail(),
                item.getCreatedAt()
        );
    }
}