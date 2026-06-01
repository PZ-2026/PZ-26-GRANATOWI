package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wejściowe raportu zakupów użytkownika.
 *
 * @param username nazwa użytkownika, którego dotyczy raport.
 * @param artistName opcjonalny filtr artysty.
 * @param dateRange zakres dat raportu.
 * @param category opcjonalny filtr kategorii.
 * @param rows wiersze danych zakupów.
 * @param totalPurchases łączna liczba zakupów.
 * @param totalAmount łączna wartość zakupów.
 */
public record UserPurchasesReportData(
        String username,
        String artistName,
        DateRange dateRange,
        String category,
        List<PurchaseRow> rows,
        int totalPurchases,
        BigDecimal totalAmount
) {
    /**
     * Pojedynczy wiersz zakupu w raporcie użytkownika.
     *
     * @param purchasedAt data i czas zakupu.
     * @param artworkTitle tytuł dzieła.
     * @param artistName nazwa artysty.
     * @param category kategoria dzieła.
     * @param price cena zakupu.
     */
    public record PurchaseRow(
            LocalDateTime purchasedAt,
            String artworkTitle,
            String artistName,
            String category,
            BigDecimal price
    ) {}
}
