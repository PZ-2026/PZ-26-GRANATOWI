package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO żądania zmiany roli użytkownika.
 */
@Data
public class UpdateUserRoleRequest {
    /**
     * Konstruktor domyślny.
     */
    public UpdateUserRoleRequest() {}

    private String role;
}
