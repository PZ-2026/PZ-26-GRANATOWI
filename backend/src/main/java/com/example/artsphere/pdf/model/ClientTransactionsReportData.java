package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ClientTransactionsReportData(
        String username,
        DateRange dateRange,
        List<TransactionRow> rows,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance
) {
    public record TransactionRow(
            LocalDateTime transactionDate,
            String title,
            BigDecimal amount,
            boolean income
    ) {}
}
