package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO żądania utworzenia lub aktualizacji dzieła sztuki.
 */
@Data
@AllArgsConstructor
public class ArtworkRequest {
    /**
     * Konstruktor domyślny.
     */
    public ArtworkRequest() {}

    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isPriceless;
    private String artist;
    private String imagePath;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal depth;
    private Integer categoryId;
}