package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO żądania logowania.
 */
@Data
public class LoginRequest {
    /**
     * Konstruktor domyślny.
     */
    public LoginRequest() {}

    private String email;
    private String password;
}
