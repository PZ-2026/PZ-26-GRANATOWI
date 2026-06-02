package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO odpowiedzi z danymi adresu.
 */
@Data
@AllArgsConstructor
public class AddressResponse {
    /**
     * Konstruktor domyślny.
     */
    public AddressResponse() {}

    private Long id;
    private Long userId;
    private String username;
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
}
