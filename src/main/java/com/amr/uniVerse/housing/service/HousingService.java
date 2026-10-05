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
    private final com.amr.uniVerse.housing.repository.HousingCommentRepository commentRepository;

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
        return HousingListingResponse.fromEntity(saved, currentUser.getId(), 0);
    }

    public List<HousingListingResponse> getAllListings(ListingType type, User currentUser) {
        List<HousingListing> listings = (type != null)
                ? housingRepository.findByTypeAndStatusOrderByCreatedAtDesc(type, HousingStatus.OPEN)
                : housingRepository.findByStatusOrderByCreatedAtDesc(HousingStatus.OPEN);

        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        return listings.stream()
                .map(l -> HousingListingResponse.fromEntity(l, currentUserId, commentRepository.countByListingId(l.getId())))
                .toList();
    }

    public HousingListingResponse getListingById(Long id, User currentUser) {
        HousingListing listing = housingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return HousingListingResponse.fromEntity(listing, currentUser.getId(), commentRepository.countByListingId(id));
    }

    public List<HousingListingResponse> getMyListings(User currentUser) {
        return housingRepository.findByPostedByIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(l -> HousingListingResponse.fromEntity(l, currentUser.getId(), commentRepository.countByListingId(l.getId())))
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
        return HousingListingResponse.fromEntity(housingRepository.save(listing), currentUser.getId(), commentRepository.countByListingId(id));
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

    // ---------- COMMENTS ----------

    @Transactional
    public com.amr.uniVerse.housing.dto.HousingCommentResponse addComment(
            Long listingId,
            com.amr.uniVerse.housing.dto.CreateHousingCommentRequest request,
            User currentUser
    ) {
        HousingListing listing = housingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        com.amr.uniVerse.housing.entity.HousingComment comment = com.amr.uniVerse.housing.entity.HousingComment.builder()
                .content(request.content().trim())
                .listing(listing)
                .author(currentUser)
                .build();

        com.amr.uniVerse.housing.entity.HousingComment saved = commentRepository.save(comment);
        return com.amr.uniVerse.housing.dto.HousingCommentResponse.fromEntity(saved);
    }

    public List<com.amr.uniVerse.housing.dto.HousingCommentResponse> getComments(Long listingId) {
        return commentRepository.findByListingIdOrderByCreatedAtAsc(listingId)
                .stream()
                .map(com.amr.uniVerse.housing.dto.HousingCommentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        com.amr.uniVerse.housing.entity.HousingComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isListingOwner = comment.getListing().getPostedBy().getId().equals(currentUser.getId());

        if (!isAuthor && !isListingOwner) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}