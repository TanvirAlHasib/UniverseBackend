package com.amr.uniVerse.feed.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.feed.dto.*;
import com.amr.uniVerse.feed.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // ---------- POSTS ----------

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(feedService.createPost(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<FeedPageResponse> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(feedService.getAllPosts(page, size, currentUser));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<FeedPageResponse> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(feedService.getUserPosts(userId, page, size, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(feedService.getPostById(id, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        feedService.deletePost(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ---------- COMMENTS ----------

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(feedService.addComment(postId, request, currentUser));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(feedService.getComments(postId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser
    ) {
        feedService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ---------- LIKES ----------

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(feedService.toggleLike(postId, currentUser));
    }
}