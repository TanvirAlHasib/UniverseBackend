package com.amr.uniVerse.housing.entity;

import com.amr.uniVerse.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "housing_listings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HousingListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType type;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private GenderPreference genderPreference;

    // Used for OFFERING listings (monthly rent)
    private Double price;

    // Used for WANTED listings
    private Double budgetMin;
    private Double budgetMax;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    private String imageUrl;

    // Comma-separated, e.g. "wifi,ac,parking,attached_bathroom"
    @Column(length = 1000)
    private String amenities;

    @Enumerated(EnumType.STRING)
    private HousingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}