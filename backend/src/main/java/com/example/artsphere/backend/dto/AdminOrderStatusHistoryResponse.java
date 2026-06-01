package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO historii statusów zamówienia dla panelu admina.
 */
@Data
@AllArgsConstructor
public class AdminOrderStatusHistoryResponse {
    private String status;
    private String date;
}
