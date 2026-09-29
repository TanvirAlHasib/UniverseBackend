package com.amr.uniVerse.housing.service;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.housing.dto.CreateHousingListingRequest;
import com.amr.uniVerse.housing.dto.HousingListingResponse;
import com.amr.uniVerse.housing.entity.HousingListing;
import com.amr.uniVerse.housing.entity.HousingStatus;
import com.amr.uniVerse.housing.entity.ListingType;
import com.amr.uniVerse.housing.repository.HousingListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HousingService {

    private final HousingListingRepository housingRepository;

    @Transactional
    public HousingListingResponse createListing(CreateHousingListingRequest request, User currentUser) {
        // Basic sanity check based on listing type
        if (request.type() == ListingType.OFFERING && request.price() == null) {
            throw new RuntimeException("Price is required when offering a room");
        }
        if (request.type() == ListingType.WANTED
                && (request.budgetMin() == null || request.budgetMax() == null)) {
            throw new RuntimeException("Budget range is required when looking for a room");
        }

        HousingListing listing = HousingListing.builder()
                .type(request.type())
                .title(request.title())
                .description(request.description())
                .location(request.location())
                .roomType(request.roomType())
                .genderPreference(request.genderPreference())
                .price(request.price())
                .budgetMin(request.budgetMin())
                .budgetMax(request.budgetMax())
                .availableFrom(request.availableFrom())
                .imageUrl(request.imageUrl())
                .amenities(request.amenities())
                .status(HousingStatus.OPEN)
                .postedBy(currentUser)
                .build();

        HousingListing saved = housingRepository.save(listing);
        return HousingListingResponse.fromEntity(saved, currentUser.getId());
    }

    public List<HousingListingResponse> getAllListings(ListingType type, User currentUser) {
        List<HousingListing> listings = (type != null)
                ? housingRepository.findByTypeAndStatusOrderByCreatedAtDesc(type, HousingStatus.OPEN)
                : housingRepository.findByStatusOrderByCreatedAtDesc(HousingStatus.OPEN);

        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        return listings.stream()
                .map(l -> HousingListingResponse.fromEntity(l, currentUserId))
                .toList();
    }

    public HousingListingResponse getListingById(Long id, User currentUser) {
        HousingListing listing = housingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return HousingListingResponse.fromEntity(listing, currentUser.getId());
    }

    public List<HousingListingResponse> getMyListings(User currentUser) {
        return housingRepository.findByPostedByIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(l -> HousingListingResponse.fromEntity(l, currentUser.getId()))
                .toList();
    }

    @Transactional
    public HousingListingResponse markAsFilled(Long id, User currentUser) {
        HousingListing listing = housingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getPostedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this listing");
        }

        listing.setStatus(HousingStatus.FILLED);
        return HousingListingResponse.fromEntity(housingRepository.save(listing), currentUser.getId());
    }

    @Transactional
    public void deleteListing(Long id, User currentUser) {
        HousingListing listing = housingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getPostedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this listing");
        }

        housingRepository.delete(listing);
    }
}