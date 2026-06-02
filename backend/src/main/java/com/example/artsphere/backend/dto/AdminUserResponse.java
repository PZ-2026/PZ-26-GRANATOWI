package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO danych użytkownika dla panelu admina.
 */
@Data
@AllArgsConstructor
public class AdminUserResponse {
    /**
     * Konstruktor domyślny.
     */
    public AdminUserResponse() {}

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Double balance;
    private LocalDateTime createdAt;
    private Boolean active;
    private Boolean verified;
}
