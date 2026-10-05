package com.amr.uniVerse.housing.dto;

import com.amr.uniVerse.housing.entity.GenderPreference;
import com.amr.uniVerse.housing.entity.HousingListing;
import com.amr.uniVerse.housing.entity.HousingStatus;
import com.amr.uniVerse.housing.entity.ListingType;
import com.amr.uniVerse.housing.entity.RoomType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HousingListingResponse(
        Long id,
        ListingType type,
        String title,
        String description,
        String location,
        RoomType roomType,
        GenderPreference genderPreference,
        Double price,
        Double budgetMin,
        Double budgetMax,
        LocalDate availableFrom,
        String imageUrl,
        String amenities,
        HousingStatus status,
        Long postedById,
        String postedByName,
        String postedByEmail,
        LocalDateTime createdAt,
        boolean ownedByMe,
        long commentCount
) {
    public static HousingListingResponse fromEntity(HousingListing listing, Long currentUserId) {
        return fromEntity(listing, currentUserId, listing.getComments() != null ? listing.getComments().size() : 0);
    }

    public static HousingListingResponse fromEntity(HousingListing listing, Long currentUserId, long commentCount) {
        boolean owned = currentUserId != null && listing.getPostedBy().getId().equals(currentUserId);
        return new HousingListingResponse(
                listing.getId(),
                listing.getType(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getLocation(),
                listing.getRoomType(),
                listing.getGenderPreference(),
                listing.getPrice(),
                listing.getBudgetMin(),
                listing.getBudgetMax(),
                listing.getAvailableFrom(),
                listing.getImageUrl(),
                listing.getAmenities(),
                listing.getStatus(),
                listing.getPostedBy().getId(),
                listing.getPostedBy().getFirstName() + " " + listing.getPostedBy().getLastName(),
                listing.getPostedBy().getEmail(),
                listing.getCreatedAt(),
                owned,
                commentCount
        );
    }
}