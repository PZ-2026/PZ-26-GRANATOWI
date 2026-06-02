package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repozytorium encji Order.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Znajduje zamówienia użytkownika.
     * @param userId identyfikator użytkownika
     * @return lista zamówień
     */
    List<Order> findByUserId(Long userId);

    /**
     * Znajduje wszystkie zamówienia posortowane malejąco według daty utworzenia.
     * @return lista zamówień
     */
    List<Order> findAllByOrderByCreatedAtDesc();
}
