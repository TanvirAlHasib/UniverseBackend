package com.amr.uniVerse.lostfound.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.lostfound.dto.CreateLostFoundRequest;
import com.amr.uniVerse.lostfound.dto.LostFoundResponse;
import com.amr.uniVerse.lostfound.entity.ItemType;
import com.amr.uniVerse.lostfound.service.LostFoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lost-found")
@RequiredArgsConstructor
public class LostFoundController {

    private final LostFoundService lostFoundService;

    @PostMapping
    public ResponseEntity<LostFoundResponse> createItem(
            @Valid @RequestBody CreateLostFoundRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(lostFoundService.createItem(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<LostFoundResponse>> getAllItems(
            @RequestParam(required = false) ItemType type
    ) {
        return ResponseEntity.ok(lostFoundService.getAllItems(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostFoundResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(lostFoundService.getItemById(id));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<LostFoundResponse> resolveItem(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(lostFoundService.resolveItem(id, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        lostFoundService.deleteItem(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}