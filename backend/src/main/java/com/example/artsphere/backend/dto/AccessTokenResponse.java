package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO odpowiedzi zawierającej nowy access token JWT.
 */
@Data
@AllArgsConstructor
public class AccessTokenResponse {
    private String accessToken;
}
