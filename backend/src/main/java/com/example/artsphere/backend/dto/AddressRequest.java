package com.example.artsphere.backend.dto;

import lombok.Data;

/**
 * DTO żądania utworzenia lub aktualizacji adresu.
 */
@Data
public class AddressRequest {
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String apartmentNumber;
}
