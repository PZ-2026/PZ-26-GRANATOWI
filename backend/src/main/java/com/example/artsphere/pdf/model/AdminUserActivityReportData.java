package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wejściowe raportu aktywności użytkowników dla administratora.
 *
 * @param dateRange zakres dat raportu.
 * @param roleFilter opcjonalny filtr roli użytkownika.
 * @param rows wiersze aktywności użytkowników.
 * @param totalUsers łączna liczba użytkowników w raporcie.
 * @param activeUsers liczba aktywnych użytkowników.
 * @param totalBalance łączne saldo użytkowników.
 */
public record AdminUserActivityReportData(
        DateRange dateRange,
        String roleFilter,
        List<UserActivityRow> rows,
        int totalUsers,
        int activeUsers,
        BigDecimal totalBalance
) {
    /**
     * Pojedynczy wiersz aktywności użytkownika w raporcie.
     *
     * @param username nazwa użytkownika.
     * @param role rola użytkownika.
     * @param registrationDate data rejestracji.
     * @param balance saldo użytkownika.
     * @param orderCount liczba zamówień użytkownika.
     * @param active flaga aktywności konta.
     */
    public record UserActivityRow(
            String username,
            String role,
            LocalDateTime registrationDate,
            BigDecimal balance,
            int orderCount,
            boolean active
    ) {}
}
