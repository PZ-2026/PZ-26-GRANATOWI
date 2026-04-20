package com.example.artsphere.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminOrderResponse {
    private Long id;
    private String orderNumber;
    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private Long sellerId;
    private String sellerName;
    private Long artworkId;
    private String artworkTitle;
    private String artworkImage;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private String status;
    private String orderDate;
    private String paymentMethod;
    private String paymentStatus;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingCountry;
    private String trackingNumber;
    private String estimatedDelivery;
    private String actualDelivery;
    private String notes;
    private List<AdminOrderStatusHistoryResponse> statusHistory;
}
