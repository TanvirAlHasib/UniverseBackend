package com.amr.uniVerse.feed.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String content,
        String imageUrl,
        Long authorId,
        String authorName,
        String authorEmail,
        long likeCount,
        long commentCount,
        boolean likedByMe,
        LocalDateTime createdAt
) {}