package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO odpowiedzi logowania zawierającej access token i dane profilu użytkownika.
 */
@Data
@AllArgsConstructor
public class AuthTokenResponse {
    private String accessToken;
    private LoginResponse user;
}
