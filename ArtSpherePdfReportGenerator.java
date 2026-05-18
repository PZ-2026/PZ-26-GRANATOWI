package com.example.artsphere.pdf;

import com.example.artsphere.pdf.model.*;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArtSpherePdfReportGenerator {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generateArtistSalesReport(ArtistSalesReportData data) {
        return buildReport(
                "Raport sprzedaży artysty",
                Map.of(
                        "Artysta", safe(data.artistName()),
                        "Zakres dat", formatDateRange(data.dateRange()),
                        "Kategoria", fallbackAll(data.category())
                ),
                List.of("Data sprzedaży", "Dzieło", "Kategoria", "Kupujący", "Kwota [PLN]"),
                data.rows().stream().map(row -> List.of(
                        formatDateTime(row.soldAt()),
                        safe(row.artworkTitle()),
                        safe(row.category()),
                        safe(row.buyerUsername()),
                        formatAmount(row.price())
                )).toList(),
                Map.of(
                        "Liczba sprzedaży", String.valueOf(data.totalSales()),
                        "Łączna wartość", formatAmount(data.totalAmount())
                )
        );
    }

    public byte[] generateSystemCommissionReport(SystemCommissionReportData data) {
        return buildReport(
                "Raport prowizji systemowej",
                Map.of(
                        "Zakres dat", formatDateRange(data.dateRange()),
                        "Kategoria", fallbackAll(data.category()),
                        "Stawka prowizji", data.commissionRate().setScale(2, RoundingMode.HALF_UP) + "%"
                ),
                List.of("Data sprzedaży", "Dzieło", "Kategoria", "Wartość brutto [PLN]", "Prowizja [PLN]", "Dla artysty [PLN]"),
                data.rows().stream().map(row -> List.of(
                        formatDateTime(row.soldAt()),
                        safe(row.artworkTitle()),
                        safe(row.category()),
                        formatAmount(row.grossAmount()),
                        formatAmount(row.commissionAmount()),
                        formatAmount(row.artistAmount())
                )).toList(),
                Map.of(
                        "Łączna sprzedaż", formatAmount(data.totalSales()),
                        "Łączna prowizja", formatAmount(data.totalCommission())
                )
        );
    }

    public byte[] generatePlatformActivityReport(PlatformActivityReportData data) {
        return buildReport(
                "Raport aktywności platformy",
                Map.of(
                        "Data", data.date().format(DATE_FORMAT),
                        "Zakres godzin", data.hourFrom().format(TIME_FORMAT) + " - " + data.hourTo().format(TIME_FORMAT)
                ),
                List.of("Czas", "Typ zdarzenia", "Użytkownik", "Szczegóły", "Kwota [PLN]"),
                data.rows().stream().map(row -> List.of(
                        formatDateTime(row.timestamp()),
                        safe(row.eventType()),
                        safe(row.username()),
                        safe(row.details()),
                        formatAmount(row.amount())
                )).toList(),
                Map.of(
                        "Liczba sprzedaży", String.valueOf(data.totalSales()),
                        "Liczba zamówień", String.valueOf(data.totalOrders()),
                        "Unikalni kupujący", String.valueOf(data.uniqueBuyers())
                )
        );
    }

    public byte[] generateUserPurchasesReport(UserPurchasesReportData data) {
        return buildReport(
                "Raport zakupów użytkownika",
                Map.of(
                        "Użytkownik", safe(data.username()),
                        "Artysta", fallbackAll(data.artistName()),
                        "Zakres dat", formatDateRange(data.dateRange()),
                        "Kategoria", fallbackAll(data.category())
                ),
                List.of("Data zakupu", "Dzieło", "Artysta", "Kategoria", "Kwota [PLN]"),
                data.rows().stream().map(row -> List.of(
                        formatDateTime(row.purchasedAt()),
                        safe(row.artworkTitle()),
                        safe(row.artistName()),
                        safe(row.category()),
                        formatAmount(row.price())
                )).toList(),
                Map.of(
                        "Liczba zakupów", String.valueOf(data.totalPurchases()),
                        "Łączna wartość zakupów", formatAmount(data.totalAmount())
                )
        );
    }

    private byte[] buildReport(
            String title,
            Map<String, String> filters,
            List<String> headers,
            List<List<String>> rows,
            Map<String, String> summary
    ) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("ArtSphere")
                .setBold()
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph(title)
                .setBold()
                .setFontSize(18)
                .setMarginTop(6)
                .setMarginBottom(8)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Wygenerowano: " + LocalDateTime.now().format(DATE_TIME_FORMAT))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(10));

        document.add(new Paragraph("Parametry raportu").setBold().setFontSize(12));
        filters.forEach((key, value) -> document.add(new Paragraph(key + ": " + safe(value)).setFontSize(10)));

        document.add(new Paragraph(" ").setMarginBottom(4));
        document.add(new Paragraph("Dane szczegółowe").setBold().setFontSize(12));
        document.add(buildDataTable(headers, rows));

        document.add(new Paragraph("Podsumowanie").setBold().setFontSize(12).setMarginTop(10));
        summary.forEach((key, value) -> document.add(new Paragraph(key + ": " + safe(value)).setFontSize(10)));

        document.close();
        return outputStream.toByteArray();
    }

    private Table buildDataTable(List<String> headers, List<List<String>> rows) {
        Table table = new Table(UnitValue.createPercentArray(headers.size()))
                .useAllAvailableWidth()
                .setFontSize(9);

        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(new DeviceGray(0.85f)));
        }

        if (rows == null || rows.isEmpty()) {
            table.addCell(new Cell(1, headers.size())
                    .add(new Paragraph("Brak danych dla wybranych filtrów."))
                    .setTextAlignment(TextAlignment.CENTER));
            return table;
        }

        for (List<String> row : rows) {
            for (String value : row) {
                table.addCell(new Cell().add(new Paragraph(safe(value))));
            }
        }

        return table;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String fallbackAll(String value) {
        return value == null || value.isBlank() ? "Wszystkie" : value;
    }

    private String formatDateRange(DateRange range) {
        return range.from().format(DATE_FORMAT) + " - " + range.to().format(DATE_FORMAT);
    }

    private String formatDateTime(LocalDateTime value) {
        return Objects.requireNonNullElse(value, LocalDateTime.MIN).equals(LocalDateTime.MIN)
                ? "-"
                : value.format(DATE_TIME_FORMAT);
    }

    private String formatAmount(BigDecimal value) {
        if (value == null) {
            return "-";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
