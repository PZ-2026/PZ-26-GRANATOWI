package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Encja dzieła sztuki.
 */
@Entity
@Table(name = "artworks")
@Data
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private BigDecimal price;

    @Column(name = "is_priceless")
    private Boolean isPriceless = false;

    private String artist;

    @Column(name = "image_path")
    private String imagePath;

    // Używamy wszedzie BigDecimal, by kompilator nie krzyczał na niezgodność z ArtworkRequest
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_sold")
    private Boolean isSold = false;

    private String status = "AVAILABLE";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}