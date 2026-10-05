package com.amr.uniVerse.marketplace.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.marketplace.dto.CreateListingRequest;
import com.amr.uniVerse.marketplace.dto.ListingResponse;
import com.amr.uniVerse.marketplace.entity.Category;
import com.amr.uniVerse.marketplace.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ListingResponse> createListing(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(listingService.createListing(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ListingResponse>> getAllListings(
            @RequestParam(required = false) Category category,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(listingService.getAllAvailableListings(category, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListingById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(listingService.getListingById(id, currentUser));
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<ListingResponse> markAsSold(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(listingService.markAsSold(id, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        listingService.deleteListing(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ---------- COMMENTS ----------

    @PostMapping("/{id}/comments")
    public ResponseEntity<com.amr.uniVerse.marketplace.dto.MarketplaceCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody com.amr.uniVerse.marketplace.dto.CreateMarketplaceCommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(listingService.addComment(id, request, currentUser));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<com.amr.uniVerse.marketplace.dto.MarketplaceCommentResponse>> getComments(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(listingService.getComments(id));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser
    ) {
        listingService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}