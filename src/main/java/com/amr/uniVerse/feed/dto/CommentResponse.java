package com.amr.uniVerse.feed.dto;

import com.amr.uniVerse.feed.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        String content,
        Long authorId,
        String authorName,
        LocalDateTime createdAt
) {
    public static CommentResponse fromEntity(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getPost().getId(),
                c.getContent(),
                c.getAuthor().getId(),
                c.getAuthor().getFirstName() + " " + c.getAuthor().getLastName(),
                c.getCreatedAt()
        );
    }
}