package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repozytorium encji OrderStatusHistory.
 */
@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    /**
     * Znajduje historię statusów zamówienia posortowaną chronologicznie.
     * @param orderId identyfikator zamówienia
     * @return lista wpisów historii statusów
     */
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Long orderId);
}
