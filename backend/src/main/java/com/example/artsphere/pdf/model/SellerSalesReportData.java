package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SellerSalesReportData(
        String sellerName,
        DateRange dateRange,
        String category,
        List<SellerSalesRow> rows,
        int totalSales,
        BigDecimal totalRevenue,
        BigDecimal averagePrice
) {
    public record SellerSalesRow(
            LocalDateTime soldAt,
            String artworkTitle,
            String category,
            String buyerUsername,
            BigDecimal price
    ) {}
}
