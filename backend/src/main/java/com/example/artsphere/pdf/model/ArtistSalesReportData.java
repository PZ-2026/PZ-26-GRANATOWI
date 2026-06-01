package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wejściowe raportu sprzedaży artysty.
 *
 * @param artistName nazwa artysty widoczna w raporcie.
 * @param dateRange zakres dat raportu.
 * @param category opcjonalny filtr kategorii.
 * @param rows wiersze danych sprzedaży.
 * @param totalSales łączna liczba sprzedaży.
 * @param totalAmount łączna wartość sprzedaży.
 */
public record ArtistSalesReportData(
        String artistName,
        DateRange dateRange,
        String category,
        List<ArtistSalesRow> rows,
        int totalSales,
        BigDecimal totalAmount
) {
    /**
     * Pojedynczy wiersz sprzedaży artysty w raporcie.
     *
     * @param soldAt data i czas sprzedaży.
     * @param artworkTitle tytuł sprzedanego dzieła.
     * @param category nazwa kategorii dzieła.
     * @param buyerUsername nazwa kupującego.
     * @param price cena sprzedaży.
     */
    public record ArtistSalesRow(
            LocalDateTime soldAt,
            String artworkTitle,
            String category,
            String buyerUsername,
            BigDecimal price
    ) {}
}
