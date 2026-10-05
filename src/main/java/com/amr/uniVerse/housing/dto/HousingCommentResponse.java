package com.amr.uniVerse.housing.dto;

import com.amr.uniVerse.housing.entity.HousingComment;

import java.time.LocalDateTime;

public record HousingCommentResponse(
        Long id,
        Long listingId,
        String content,
        Long authorId,
        String authorName,
        LocalDateTime createdAt
) {
    public static HousingCommentResponse fromEntity(HousingComment c) {
        return new HousingCommentResponse(
                c.getId(),
                c.getListing().getId(),
                c.getContent(),
                c.getAuthor().getId(),
                c.getAuthor().getFirstName() + " " + c.getAuthor().getLastName(),
                c.getCreatedAt()
        );
    }
}
