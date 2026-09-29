package com.amr.uniVerse.studyroom.repository;

import com.amr.uniVerse.studyroom.entity.MembershipStatus;
import com.amr.uniVerse.studyroom.entity.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {
    Optional<RoomMembership> findByRoomIdAndUserId(Long roomId, Long userId);
    List<RoomMembership> findByRoomIdAndStatus(Long roomId, MembershipStatus status);
    long countByRoomIdAndStatus(Long roomId, MembershipStatus status);
}