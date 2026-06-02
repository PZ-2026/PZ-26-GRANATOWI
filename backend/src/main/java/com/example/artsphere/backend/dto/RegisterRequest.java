package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO żądania rejestracji.
 */
@Data
public class RegisterRequest {
    /**
     * Konstruktor domyślny.
     */
    public RegisterRequest() {}

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String roleName; // "BUYER" or "ARTIST"
}
