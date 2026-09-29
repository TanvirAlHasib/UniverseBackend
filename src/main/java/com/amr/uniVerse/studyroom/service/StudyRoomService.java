package com.amr.uniVerse.studyroom.service;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.studyroom.dto.CreateStudyRoomRequest;
import com.amr.uniVerse.studyroom.dto.MembershipResponse;
import com.amr.uniVerse.studyroom.dto.StudyRoomResponse;
import com.amr.uniVerse.studyroom.entity.*;
import com.amr.uniVerse.studyroom.repository.RoomMembershipRepository;
import com.amr.uniVerse.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final RoomMembershipRepository membershipRepository;

    @Transactional
    public StudyRoomResponse createRoom(CreateStudyRoomRequest request, User currentUser) {
        StudyRoom room = StudyRoom.builder()
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .maxMembers(request.maxMembers())
                .creator(currentUser)
                .build();

        studyRoomRepository.save(room);

        RoomMembership membership = RoomMembership.builder()
                .room(room)
                .user(currentUser)
                .status(MembershipStatus.APPROVED)
                .joinedAt(LocalDateTime.now())
                .build();
        membershipRepository.save(membership);

        return mapToResponse(room, currentUser);
    }

    public List<StudyRoomResponse> getAllRooms(RoomCategory category, User currentUser) {
        List<StudyRoom> rooms = (category != null)
                ? studyRoomRepository.findByCategoryOrderByCreatedAtDesc(category)
                : studyRoomRepository.findAllByOrderByCreatedAtDesc();

        return rooms.stream().map(room -> mapToResponse(room, currentUser)).toList();
    }

    public StudyRoomResponse getRoomById(Long id, User currentUser) {
        StudyRoom room = studyRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return mapToResponse(room, currentUser);
    }

    @Transactional
    public StudyRoomResponse requestToJoin(Long roomId, User currentUser) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are the creator of this room");
        }

        RoomMembership membership = membershipRepository
                .findByRoomIdAndUserId(roomId, currentUser.getId())
                .orElse(null);

        if (membership != null && membership.getStatus() == MembershipStatus.APPROVED) {
            throw new RuntimeException("You are already a member of this room");
        }

        if (membership != null && membership.getStatus() == MembershipStatus.PENDING) {
            throw new RuntimeException("Your request is already pending");
        }

        if (membership == null) {
            membership = RoomMembership.builder()
                    .room(room)
                    .user(currentUser)
                    .status(MembershipStatus.PENDING)
                    .build();
        } else {
            membership.setStatus(MembershipStatus.PENDING);
        }

        membershipRepository.save(membership);
        return mapToResponse(room, currentUser);
    }

    @Transactional
    public StudyRoomResponse approveRequest(Long roomId, Long userId, User currentUser) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can approve requests");
        }

        RoomMembership membership = membershipRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("No membership request found"));

        membership.setStatus(MembershipStatus.APPROVED);
        membership.setJoinedAt(LocalDateTime.now());
        membershipRepository.save(membership);

        return mapToResponse(room, currentUser);
    }

    @Transactional
    public StudyRoomResponse rejectRequest(Long roomId, Long userId, User currentUser) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can reject requests");
        }

        RoomMembership membership = membershipRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("No membership request found"));

        membership.setStatus(MembershipStatus.REJECTED);
        membershipRepository.save(membership);

        return mapToResponse(room, currentUser);
    }

    public List<MembershipResponse> getPendingRequests(Long roomId, User currentUser) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can view pending requests");
        }

        return membershipRepository.findByRoomIdAndStatus(roomId, MembershipStatus.PENDING)
                .stream()
                .map(MembershipResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteRoom(Long roomId, User currentUser) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (!room.getCreator().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the creator can delete this room");
        }

        studyRoomRepository.delete(room);
    }

    // Helper method to map the entity to a DTO
    private StudyRoomResponse mapToResponse(StudyRoom room, User currentUser) {
        long approvedCount = membershipRepository.countByRoomIdAndStatus(room.getId(), MembershipStatus.APPROVED);

        boolean isMember = false;
        MembershipStatus status = null;

        if (currentUser != null) {
            var membershipOpt = membershipRepository.findByRoomIdAndUserId(room.getId(), currentUser.getId());
            if (membershipOpt.isPresent()) {
                status = membershipOpt.get().getStatus();
                isMember = (status == MembershipStatus.APPROVED);
            }
        }

        return new StudyRoomResponse(
                room.getId(),
                room.getTitle(),
                room.getDescription(),
                room.getCategory(),
                room.getMaxMembers(),
                approvedCount,
                room.getCreator().getFirstName() + " " + room.getCreator().getLastName(),
                room.getCreatedAt(),
                isMember,
                status
        );
    }
}