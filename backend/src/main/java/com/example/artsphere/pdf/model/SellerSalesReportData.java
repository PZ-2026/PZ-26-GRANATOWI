package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wejściowe raportu sprzedaży sprzedawcy.
 *
 * @param sellerName nazwa sprzedawcy.
 * @param dateRange zakres dat raportu.
 * @param category opcjonalny filtr kategorii.
 * @param rows wiersze danych sprzedaży.
 * @param totalSales łączna liczba sprzedaży.
 * @param totalRevenue łączny przychód.
 * @param averagePrice średnia cena sprzedaży.
 */
public record SellerSalesReportData(
        String sellerName,
        DateRange dateRange,
        String category,
        List<SellerSalesRow> rows,
        int totalSales,
        BigDecimal totalRevenue,
        BigDecimal averagePrice
) {
    /**
     * Pojedynczy wiersz sprzedaży sprzedawcy w raporcie.
     *
     * @param soldAt data i czas sprzedaży.
     * @param artworkTitle tytuł dzieła.
     * @param category kategoria dzieła.
     * @param buyerUsername nazwa kupującego.
     * @param price kwota sprzedaży.
     */
    public record SellerSalesRow(
            LocalDateTime soldAt,
            String artworkTitle,
            String category,
            String buyerUsername,
            BigDecimal price
    ) {}
}
