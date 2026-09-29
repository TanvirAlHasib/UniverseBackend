package com.amr.uniVerse.studyroom.repository;

import com.amr.uniVerse.studyroom.entity.RoomCategory;
import com.amr.uniVerse.studyroom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
    List<StudyRoom> findByCategoryOrderByCreatedAtDesc(RoomCategory category);
    List<StudyRoom> findAllByOrderByCreatedAtDesc();
}