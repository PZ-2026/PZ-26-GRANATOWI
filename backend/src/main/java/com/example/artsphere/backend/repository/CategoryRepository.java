package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Spring automatycznie generuje podstawowe metody
}