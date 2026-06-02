package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repozytorium encji Category.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    /**
     * Znajduje kategorie na podstawie identyfikatora kategorii nadrzędnej.
     * @param parentId identyfikator kategorii nadrzędnej
     * @return lista kategorii
     */
    List<Category> findByParentId(Integer parentId);
}