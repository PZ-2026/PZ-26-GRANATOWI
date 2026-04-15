package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;
    private String slug;
    private Integer parentId;
    private String parentName;
    private Boolean isActive;
    private Integer artworkCount;
    private Integer soldArtworkCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private Integer displayOrder;
    private String iconName;
    private String color;
}