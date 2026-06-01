package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wejściowe raportu transakcji portfela użytkownika.
 *
 * @param username nazwa użytkownika.
 * @param dateRange zakres dat raportu.
 * @param rows wiersze transakcji.
 * @param totalIncome suma przychodów.
 * @param totalExpenses suma wydatków.
 * @param balance saldo końcowe użytkownika.
 */
public record ClientTransactionsReportData(
        String username,
        DateRange dateRange,
        List<TransactionRow> rows,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance
) {
    /**
     * Pojedynczy wiersz transakcji w raporcie portfela.
     *
     * @param transactionDate data i czas transakcji.
     * @param title tytuł transakcji.
     * @param amount kwota transakcji.
     * @param income flaga określająca, czy transakcja jest przychodem.
     */
    public record TransactionRow(
            LocalDateTime transactionDate,
            String title,
            BigDecimal amount,
            boolean income
    ) {}
}
