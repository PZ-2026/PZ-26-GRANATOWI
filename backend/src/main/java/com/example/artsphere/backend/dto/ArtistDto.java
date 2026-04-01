package com.example.artsphere.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ArtistDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}