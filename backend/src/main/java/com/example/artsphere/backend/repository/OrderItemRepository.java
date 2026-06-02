package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repozytorium encji OrderItem.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * Znajduje elementy zamówienia na podstawie identyfikatora zamówienia.
     * @param orderId identyfikator zamówienia
     * @return lista elementów zamówienia
     */
    List<OrderItem> findByOrderId(Long orderId);
}