package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wejściowe raportu prowizji systemowych.
 *
 * @param dateRange zakres dat raportu.
 * @param category opcjonalny filtr kategorii.
 * @param commissionRate stawka prowizji w procentach.
 * @param rows wiersze danych prowizji.
 * @param totalSales łączna wartość sprzedaży brutto.
 * @param totalCommission łączna wartość prowizji.
 */
public record SystemCommissionReportData(
        DateRange dateRange,
        String category,
        BigDecimal commissionRate,
        List<CommissionRow> rows,
        BigDecimal totalSales,
        BigDecimal totalCommission
) {
    /**
     * Pojedynczy wiersz prowizji w raporcie.
     *
     * @param soldAt data i czas sprzedaży.
     * @param artworkTitle tytuł dzieła.
     * @param category kategoria dzieła.
     * @param grossAmount wartość brutto sprzedaży.
     * @param commissionAmount wartość prowizji.
     * @param artistAmount kwota należna artyście.
     */
    public record CommissionRow(
            LocalDateTime soldAt,
            String artworkTitle,
            String category,
            BigDecimal grossAmount,
            BigDecimal commissionAmount,
            BigDecimal artistAmount
    ) {}
}
