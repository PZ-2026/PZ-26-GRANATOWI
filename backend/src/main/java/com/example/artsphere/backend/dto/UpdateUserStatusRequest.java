package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO żądania zmiany statusu użytkownika.
 */
@Data
public class UpdateUserStatusRequest {
    private Boolean active;
}
