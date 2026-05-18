package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SystemCommissionReportData(
        DateRange dateRange,
        String category,
        BigDecimal commissionRate,
        List<CommissionRow> rows,
        BigDecimal totalSales,
        BigDecimal totalCommission
) {
    public record CommissionRow(
            LocalDateTime soldAt,
            String artworkTitle,
            String category,
            BigDecimal grossAmount,
            BigDecimal commissionAmount,
            BigDecimal artistAmount
    ) {}
}
