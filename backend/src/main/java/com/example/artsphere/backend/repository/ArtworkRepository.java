package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repozytorium encji Artwork.
 */
@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    
    // Znajdź wszystkie dzieła danego użytkownika (sprzedawcy)
    List<Artwork> findByUserId(Long userId);
    
    // Znajdź dzieła dostępne (nie sprzedane)
    List<Artwork> findByIsSoldFalse();
    
    // Znajdź dzieła danego użytkownika które nie są sprzedane
    List<Artwork> findByUserIdAndIsSoldFalse(Long userId);
    
    // Znajdź dzieła według kategorii
    List<Artwork> findByCategoryId(Integer categoryId);

    // Liczba dzieł w danej kategorii
    int countByCategoryId(Integer categoryId);

    // Liczba sprzedanych dzieł w danej kategorii
    int countByCategoryIdAndIsSoldTrue(Integer categoryId);
}