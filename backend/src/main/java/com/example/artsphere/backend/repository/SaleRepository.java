package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repozytorium encji Sale.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findTopByArtworkIdAndBuyerIdOrderBySoldAtDesc(Long artworkId, Long buyerId);
}
