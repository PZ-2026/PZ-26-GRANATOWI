package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isPriceless;
    private String artist;
    private String imagePath;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
    private Long userId;
    private String userUsername; // Username sprzedawcy
    private Integer categoryId;
    private String categoryName;
    private Boolean isSold;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}