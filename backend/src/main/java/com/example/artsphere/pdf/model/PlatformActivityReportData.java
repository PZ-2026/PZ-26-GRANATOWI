package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Dane wejściowe raportu aktywności platformy.
 *
 * @param date data raportu.
 * @param hourFrom godzina początkowa zakresu.
 * @param hourTo godzina końcowa zakresu.
 * @param rows wiersze aktywności.
 * @param totalSales liczba sprzedaży w raporcie.
 * @param totalOrders liczba zamówień w raporcie.
 * @param uniqueBuyers liczba unikalnych kupujących.
 */
public record PlatformActivityReportData(
        LocalDate date,
        LocalTime hourFrom,
        LocalTime hourTo,
        List<ActivityRow> rows,
        int totalSales,
        int totalOrders,
        int uniqueBuyers
) {
    /**
     * Pojedynczy wiersz aktywności w raporcie.
     *
     * @param timestamp czas zdarzenia.
     * @param eventType typ zdarzenia.
     * @param username nazwa użytkownika powiązanego ze zdarzeniem.
     * @param details szczegóły zdarzenia.
     * @param amount kwota powiązana ze zdarzeniem.
     */
    public record ActivityRow(
            LocalDateTime timestamp,
            String eventType,
            String username,
            String details,
            BigDecimal amount
    ) {}
}
