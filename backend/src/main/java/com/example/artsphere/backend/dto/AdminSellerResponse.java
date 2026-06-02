package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO danych sprzedawcy dla panelu admina.
 */
@Data
@AllArgsConstructor
public class AdminSellerResponse {
    /**
     * Konstruktor domyślny.
     */
    public AdminSellerResponse() {}

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDateTime createdAt;
    private Boolean active;
    private Boolean verified;
    
    // Seller specific stats
    private Integer followerCount;
    private Integer totalArtworks;
    private Double totalRevenue;
    private Float averageRating;
}
