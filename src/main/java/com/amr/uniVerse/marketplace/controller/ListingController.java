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
            @RequestParam(required = false) Category category
    ) {
        return ResponseEntity.ok(listingService.getAllAvailableListings(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListingById(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListingById(id));
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
}