package com.example.artsphere.pdf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminUserActivityReportData(
        DateRange dateRange,
        String roleFilter,
        List<UserActivityRow> rows,
        int totalUsers,
        int activeUsers,
        BigDecimal totalBalance
) {
    public record UserActivityRow(
            String username,
            String role,
            LocalDateTime registrationDate,
            BigDecimal balance,
            int orderCount,
            boolean active
    ) {}
}
