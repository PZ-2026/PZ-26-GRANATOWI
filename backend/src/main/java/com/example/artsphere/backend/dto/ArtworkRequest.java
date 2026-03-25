package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkRequest {
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