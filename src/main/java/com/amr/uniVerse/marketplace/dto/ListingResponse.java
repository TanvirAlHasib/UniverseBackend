package com.amr.uniVerse.marketplace.dto;

import com.amr.uniVerse.marketplace.entity.Category;
import com.amr.uniVerse.marketplace.entity.ItemCondition;
import com.amr.uniVerse.marketplace.entity.Listing;
import com.amr.uniVerse.marketplace.entity.ListingStatus;

import java.time.LocalDateTime;

public record ListingResponse(
        Long id,
        String title,
        String description,
        Double price,
        Category category,
        ItemCondition condition,
        ListingStatus status,
        String location,
        String imageUrl,
        Long sellerId,
        String sellerName,
        String sellerEmail,
        LocalDateTime createdAt,
        boolean isOwner,
        long commentCount
) {
    public static ListingResponse fromEntity(Listing listing) {
        return fromEntity(listing, null, listing.getComments() != null ? listing.getComments().size() : 0);
    }

    public static ListingResponse fromEntity(Listing listing, Long currentUserId, long commentCount) {
        boolean owned = currentUserId != null && listing.getSeller().getId().equals(currentUserId);
        return new ListingResponse(
                listing.getId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getPrice(),
                listing.getCategory(),
                listing.getCondition(),
                listing.getStatus(),
                listing.getLocation(),
                listing.getImageUrl(),
                listing.getSeller().getId(),
                listing.getSeller().getFirstName() + " " + listing.getSeller().getLastName(),
                listing.getSeller().getEmail(),
                listing.getCreatedAt(),
                owned,
                commentCount
        );
    }
}