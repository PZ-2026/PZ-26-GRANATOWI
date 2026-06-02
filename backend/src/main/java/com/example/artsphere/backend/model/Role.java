package com.example.artsphere.backend.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Encja roli użytkownika.
 */
@Entity
@Table(name = "roles")
@Data
public class Role {
    /**
     * Konstruktor domyślny.
     */
    public Role() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;
}