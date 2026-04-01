package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "seller_user_follows")
@Data
public class SellerUserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
}