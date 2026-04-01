package com.example.artsphere.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;
    private Double totalPrice;
    private List<Long> artworkIds;
}