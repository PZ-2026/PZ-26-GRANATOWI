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
    
    /**
     * Znajdź wszystkie dzieła danego użytkownika (sprzedawcy).
     * @param userId identyfikator użytkownika
     * @return lista dzieł
     */
    List<Artwork> findByUserId(Long userId);
    
    /**
     * Znajdź dzieła dostępne (nie sprzedane).
     * @return lista dostępnych dzieł
     */
    List<Artwork> findByIsSoldFalse();
    
    /**
     * Znajdź dzieła danego użytkownika które nie są sprzedane.
     * @param userId identyfikator użytkownika
     * @return lista niesprzedanych dzieł użytkownika
     */
    List<Artwork> findByUserIdAndIsSoldFalse(Long userId);
    
    /**
     * Znajdź dzieła według kategorii.
     * @param categoryId identyfikator kategorii
     * @return lista dzieł w kategorii
     */
    List<Artwork> findByCategoryId(Integer categoryId);

    /**
     * Liczba dzieł w danej kategorii.
     * @param categoryId identyfikator kategorii
     * @return liczba dzieł
     */
    int countByCategoryId(Integer categoryId);

    /**
     * Liczba sprzedanych dzieł w danej kategorii.
     * @param categoryId identyfikator kategorii
     * @return liczba sprzedanych dzieł
     */
    int countByCategoryIdAndIsSoldTrue(Integer categoryId);
}