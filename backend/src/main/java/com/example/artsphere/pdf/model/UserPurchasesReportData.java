package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UserPurchasesReportData(
        String username,
        String artistName,
        DateRange dateRange,
        String category,
        List<PurchaseRow> rows,
        int totalPurchases,
        BigDecimal totalAmount
) {
    public record PurchaseRow(
            LocalDateTime purchasedAt,
            String artworkTitle,
            String artistName,
            String category,
            BigDecimal price
    ) {}
}
