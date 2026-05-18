package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ArtistSalesReportData(
        String artistName,
        DateRange dateRange,
        String category,
        List<ArtistSalesRow> rows,
        int totalSales,
        BigDecimal totalAmount
) {
    public record ArtistSalesRow(
            LocalDateTime soldAt,
            String artworkTitle,
            String category,
            String buyerUsername,
            BigDecimal price
    ) {}
}
