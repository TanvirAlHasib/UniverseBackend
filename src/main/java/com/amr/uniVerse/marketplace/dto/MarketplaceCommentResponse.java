package com.amr.uniVerse.marketplace.dto;

import com.amr.uniVerse.marketplace.entity.ListingComment;

import java.time.LocalDateTime;

public record MarketplaceCommentResponse(
        Long id,
        Long listingId,
        String content,
        Long authorId,
        String authorName,
        LocalDateTime createdAt
) {
    public static MarketplaceCommentResponse fromEntity(ListingComment c) {
        return new MarketplaceCommentResponse(
                c.getId(),
                c.getListing().getId(),
                c.getContent(),
                c.getAuthor().getId(),
                c.getAuthor().getFirstName() + " " + c.getAuthor().getLastName(),
                c.getCreatedAt()
        );
    }
}
