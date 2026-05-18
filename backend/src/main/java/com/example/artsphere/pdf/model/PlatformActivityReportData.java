package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record PlatformActivityReportData(
        LocalDate date,
        LocalTime hourFrom,
        LocalTime hourTo,
        List<ActivityRow> rows,
        int totalSales,
        int totalOrders,
        int uniqueBuyers
) {
    public record ActivityRow(
            LocalDateTime timestamp,
            String eventType,
            String username,
            String details,
            BigDecimal amount
    ) {}
}
