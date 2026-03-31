package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private BigDecimal balance = BigDecimal.ZERO;

    // Zamiana z relacji na zwykły string
    @Column(name = "role")
    private String role;
}