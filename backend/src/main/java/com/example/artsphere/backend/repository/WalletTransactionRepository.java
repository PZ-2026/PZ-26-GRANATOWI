package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repozytorium encji WalletTransaction.
 */
@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    // Zapytanie pobierze wpłaty użytkownika, sortując od najnowszej
    List<WalletTransaction> findByUserIdOrderByTransactionDateDesc(Long userId);
}