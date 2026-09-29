package com.amr.uniVerse.marketplace.repository;

import com.amr.uniVerse.marketplace.entity.Category;
import com.amr.uniVerse.marketplace.entity.Listing;
import com.amr.uniVerse.marketplace.entity.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByStatusOrderByCreatedAtDesc(ListingStatus status);
    List<Listing> findByCategoryAndStatusOrderByCreatedAtDesc(Category category, ListingStatus status);
}