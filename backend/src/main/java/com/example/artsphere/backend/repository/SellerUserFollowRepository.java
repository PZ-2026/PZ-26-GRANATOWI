package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.SellerUserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium encji SellerUserFollow.
 */
@Repository
public interface SellerUserFollowRepository extends JpaRepository<SellerUserFollow, Long> {
    /**
     * Znajduje obserwację sprzedawcy przez użytkownika.
     * @param userId identyfikator użytkownika
     * @param sellerId identyfikator sprzedawcy
     * @return opcjonalna obserwacja
     */
    Optional<SellerUserFollow> findByUserIdAndSellerId(Long userId, Long sellerId);

    /**
     * Znajduje wszystkich sprzedawców obserwowanych przez użytkownika.
     * @param userId identyfikator użytkownika
     * @return lista obserwacji
     */
    List<SellerUserFollow> findByUserId(Long userId);

    /**
     * Znajduje wszystkich użytkowników obserwujących danego sprzedawcę.
     * @param sellerId identyfikator sprzedawcy
     * @return lista obserwacji
     */
    List<SellerUserFollow> findBySellerId(Long sellerId); // <- NOWA METODA
}