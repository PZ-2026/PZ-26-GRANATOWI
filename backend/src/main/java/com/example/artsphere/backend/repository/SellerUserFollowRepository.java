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
    Optional<SellerUserFollow> findByUserIdAndSellerId(Long userId, Long sellerId);
    List<SellerUserFollow> findByUserId(Long userId);
    List<SellerUserFollow> findBySellerId(Long sellerId); // <- NOWA METODA
}