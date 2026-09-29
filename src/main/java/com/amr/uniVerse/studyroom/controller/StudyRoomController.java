package com.amr.uniVerse.studyroom.controller;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.studyroom.dto.CreateStudyRoomRequest;
import com.amr.uniVerse.studyroom.dto.MembershipResponse;
import com.amr.uniVerse.studyroom.dto.StudyRoomResponse;
import com.amr.uniVerse.studyroom.entity.RoomCategory;
import com.amr.uniVerse.studyroom.service.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping
    public ResponseEntity<StudyRoomResponse> createRoom(
            @Valid @RequestBody CreateStudyRoomRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.createRoom(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<StudyRoomResponse>> getAllRooms(
            @RequestParam(required = false) RoomCategory category,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.getAllRooms(category, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomResponse> getRoomById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.getRoomById(id, currentUser));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<StudyRoomResponse> requestToJoin(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.requestToJoin(id, currentUser));
    }

    @GetMapping("/{id}/requests")
    public ResponseEntity<List<MembershipResponse>> getPendingRequests(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.getPendingRequests(id, currentUser));
    }

    @PatchMapping("/{id}/requests/{userId}/approve")
    public ResponseEntity<StudyRoomResponse> approveRequest(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.approveRequest(id, userId, currentUser));
    }

    @PatchMapping("/{id}/requests/{userId}/reject")
    public ResponseEntity<StudyRoomResponse> rejectRequest(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(studyRoomService.rejectRequest(id, userId, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        studyRoomService.deleteRoom(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}