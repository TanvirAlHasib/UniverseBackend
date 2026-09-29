package com.amr.uniVerse.marketplace.service;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.marketplace.dto.CreateListingRequest;
import com.amr.uniVerse.marketplace.dto.ListingResponse;
import com.amr.uniVerse.marketplace.entity.Category;
import com.amr.uniVerse.marketplace.entity.Listing;
import com.amr.uniVerse.marketplace.entity.ListingStatus;
import com.amr.uniVerse.marketplace.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    @Transactional
    public ListingResponse createListing(CreateListingRequest request, User currentUser) {
        Listing listing = Listing.builder()
                .title(request.title())
                .description(request.description())
                .price(request.price())
                .category(request.category())
                .condition(request.condition())
                .status(ListingStatus.AVAILABLE)
                .location(request.location())
                .imageUrl(request.imageUrl())
                .seller(currentUser)
                .build();

        Listing saved = listingRepository.save(listing);
        return ListingResponse.fromEntity(saved);
    }

    public List<ListingResponse> getAllAvailableListings(Category category) {
        List<Listing> listings;
        if (category != null) {
            listings = listingRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, ListingStatus.AVAILABLE);
        } else {
            listings = listingRepository.findByStatusOrderByCreatedAtDesc(ListingStatus.AVAILABLE);
        }
        return listings.stream().map(ListingResponse::fromEntity).toList();
    }

    public ListingResponse getListingById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return ListingResponse.fromEntity(listing);
    }

    @Transactional
    public ListingResponse markAsSold(Long id, User currentUser) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this listing");
        }

        listing.setStatus(ListingStatus.SOLD);
        Listing updated = listingRepository.save(listing);
        return ListingResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteListing(Long id, User currentUser) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this listing");
        }

        listingRepository.delete(listing);
    }
}