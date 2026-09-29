package com.amr.uniVerse.lostfound.repository;

import com.amr.uniVerse.lostfound.entity.ItemStatus;
import com.amr.uniVerse.lostfound.entity.ItemType;
import com.amr.uniVerse.lostfound.entity.LostFoundItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostFoundRepository extends JpaRepository<LostFoundItem, Long> {
    List<LostFoundItem> findByStatusOrderByCreatedAtDesc(ItemStatus status);
    List<LostFoundItem> findByTypeAndStatusOrderByCreatedAtDesc(ItemType type, ItemStatus status);
}