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
    private final com.amr.uniVerse.marketplace.repository.ListingCommentRepository commentRepository;

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
        return ListingResponse.fromEntity(saved, currentUser.getId(), 0);
    }

    public List<ListingResponse> getAllAvailableListings(Category category, User currentUser) {
        List<Listing> listings;
        if (category != null) {
            listings = listingRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, ListingStatus.AVAILABLE);
        } else {
            listings = listingRepository.findByStatusOrderByCreatedAtDesc(ListingStatus.AVAILABLE);
        }
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        return listings.stream()
                .map(l -> ListingResponse.fromEntity(l, currentUserId, commentRepository.countByListingId(l.getId())))
                .toList();
    }

    public ListingResponse getListingById(Long id, User currentUser) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        return ListingResponse.fromEntity(listing, currentUserId, commentRepository.countByListingId(id));
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
        return ListingResponse.fromEntity(updated, currentUser.getId(), commentRepository.countByListingId(id));
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

    // ---------- COMMENTS ----------

    @Transactional
    public com.amr.uniVerse.marketplace.dto.MarketplaceCommentResponse addComment(
            Long listingId,
            com.amr.uniVerse.marketplace.dto.CreateMarketplaceCommentRequest request,
            User currentUser
    ) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        com.amr.uniVerse.marketplace.entity.ListingComment comment = com.amr.uniVerse.marketplace.entity.ListingComment.builder()
                .content(request.content().trim())
                .listing(listing)
                .author(currentUser)
                .build();

        com.amr.uniVerse.marketplace.entity.ListingComment saved = commentRepository.save(comment);
        return com.amr.uniVerse.marketplace.dto.MarketplaceCommentResponse.fromEntity(saved);
    }

    public List<com.amr.uniVerse.marketplace.dto.MarketplaceCommentResponse> getComments(Long listingId) {
        return commentRepository.findByListingIdOrderByCreatedAtAsc(listingId)
                .stream()
                .map(com.amr.uniVerse.marketplace.dto.MarketplaceCommentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        com.amr.uniVerse.marketplace.entity.ListingComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isSeller = comment.getListing().getSeller().getId().equals(currentUser.getId());

        if (!isAuthor && !isSeller) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}