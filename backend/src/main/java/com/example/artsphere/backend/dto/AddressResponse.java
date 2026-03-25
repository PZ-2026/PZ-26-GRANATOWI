package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private Long userId;
    private String username; // Nazwa użytkownika dla admina
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
}
