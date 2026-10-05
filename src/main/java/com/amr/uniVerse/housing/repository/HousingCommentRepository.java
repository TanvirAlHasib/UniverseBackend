package com.amr.uniVerse.housing.repository;

import com.amr.uniVerse.housing.entity.HousingComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingCommentRepository extends JpaRepository<HousingComment, Long> {
    List<HousingComment> findByListingIdOrderByCreatedAtAsc(Long listingId);
    long countByListingId(Long listingId);
}
