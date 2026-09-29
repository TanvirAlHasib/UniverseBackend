package com.amr.uniVerse.feed.dto;

import java.util.List;

public record FeedPageResponse(
        List<PostResponse> posts,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext
) {}