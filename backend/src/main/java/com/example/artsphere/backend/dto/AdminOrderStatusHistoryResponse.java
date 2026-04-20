package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOrderStatusHistoryResponse {
    private String status;
    private String date;
}
