package com.example.artsphere.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO danych artysty.
 */
@Data @AllArgsConstructor
public class ArtistDto {
    /**
     * Konstruktor domyślny.
     */
    public ArtistDto() {}

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}