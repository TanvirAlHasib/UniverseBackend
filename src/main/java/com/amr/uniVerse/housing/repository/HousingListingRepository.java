package com.amr.uniVerse.housing.repository;

import com.amr.uniVerse.housing.entity.HousingListing;
import com.amr.uniVerse.housing.entity.HousingStatus;
import com.amr.uniVerse.housing.entity.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HousingListingRepository extends JpaRepository<HousingListing, Long> {
    List<HousingListing> findByStatusOrderByCreatedAtDesc(HousingStatus status);
    List<HousingListing> findByTypeAndStatusOrderByCreatedAtDesc(ListingType type, HousingStatus status);
    List<HousingListing> findByPostedByIdOrderByCreatedAtDesc(Long userId);
}