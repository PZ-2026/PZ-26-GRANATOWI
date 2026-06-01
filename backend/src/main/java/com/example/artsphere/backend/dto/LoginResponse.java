package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO odpowiedzi logowania i profilu użytkownika.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String message;
    private BigDecimal balance;
}