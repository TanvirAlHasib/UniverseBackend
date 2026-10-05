package com.amr.uniVerse.housing.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.housing.dto.CreateHousingListingRequest;
import com.amr.uniVerse.housing.dto.HousingListingResponse;
import com.amr.uniVerse.housing.entity.ListingType;
import com.amr.uniVerse.housing.service.HousingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/housing")
@RequiredArgsConstructor
public class HousingController {

    private final HousingService housingService;

    @PostMapping
    public ResponseEntity<HousingListingResponse> createListing(
            @Valid @RequestBody CreateHousingListingRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(housingService.createListing(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<HousingListingResponse>> getAllListings(
            @RequestParam(required = false) ListingType type,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(housingService.getAllListings(type, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HousingListingResponse> getListingById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(housingService.getListingById(id, currentUser));
    }

    @GetMapping("/my-listings")
    public ResponseEntity<List<HousingListingResponse>> getMyListings(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(housingService.getMyListings(currentUser));
    }

    @PatchMapping("/{id}/filled")
    public ResponseEntity<HousingListingResponse> markAsFilled(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(housingService.markAsFilled(id, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        housingService.deleteListing(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ---------- COMMENTS ----------

    @PostMapping("/{id}/comments")
    public ResponseEntity<com.amr.uniVerse.housing.dto.HousingCommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody com.amr.uniVerse.housing.dto.CreateHousingCommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(housingService.addComment(id, request, currentUser));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<com.amr.uniVerse.housing.dto.HousingCommentResponse>> getComments(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(housingService.getComments(id));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser
    ) {
        housingService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }
}