package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO żądania zmiany statusu użytkownika.
 */
@Data
public class UpdateUserStatusRequest {
    /**
     * Konstruktor domyślny.
     */
    public UpdateUserStatusRequest() {}

    private Boolean active;
}
