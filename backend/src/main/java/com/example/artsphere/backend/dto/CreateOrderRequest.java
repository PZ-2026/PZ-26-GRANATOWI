package com.example.artsphere.backend.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO żądania utworzenia zamówienia.
 */
@Data
public class CreateOrderRequest {
    /**
     * Konstruktor domyślny.
     */
    public CreateOrderRequest() {}

    private Long userId;
    private Double totalPrice;
    private List<Long> artworkIds;
    private Long addressId;
    private String paymentMethod;
}
