package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Kontroler REST do generowania raportów PDF.
 *
 * @author ArtSphere Team
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    /**
     * Konstruktor domyślny.
     */
    public ReportController() {}

    @Autowired
    private ReportService reportService;

    // ==================== RAPORTY KLIENTA (BUYER) ====================

    /**
     * Pobiera raport zakupów klienta w formacie PDF dla wskazanego zakresu dat.
     *
     * @param userId identyfikator klienta, dla którego tworzony jest raport.
     * @param dateFrom data początkowa zakresu raportu (włącznie).
     * @param dateTo data końcowa zakresu raportu (włącznie).
     * @return odpowiedź HTTP zawierająca plik PDF jako tablicę bajtów.
     */
    @GetMapping("/client/{userId}/purchases")
    public ResponseEntity<byte[]> getClientPurchaseReport(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        byte[] pdfBytes = reportService.generateClientPurchaseReport(userId, dateFrom, dateTo);
        return buildPdfResponse(pdfBytes, "raport_zakupow_uzytkownika_" + userId + ".pdf");
    }

    /**
     * Pobiera raport transakcji portfela klienta w formacie PDF.
     *
     * @param userId identyfikator klienta, dla którego tworzony jest raport.
     * @param dateFrom data początkowa zakresu raportu (włącznie).
     * @param dateTo data końcowa zakresu raportu (włącznie).
     * @return odpowiedź HTTP zawierająca plik PDF jako tablicę bajtów.
     */
    @GetMapping("/client/{userId}/transactions")
    public ResponseEntity<byte[]> getClientTransactionsReport(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        byte[] pdfBytes = reportService.generateClientTransactionsReport(userId, dateFrom, dateTo);
        return buildPdfResponse(pdfBytes, "raport_transakcji_uzytkownika_" + userId + ".pdf");
    }

    // ==================== RAPORTY SPRZEDAWCY (ARTIST) ====================

    /**
     * Pobiera raport sprzedaży sprzedawcy w formacie PDF.
     *
     * @param sellerId identyfikator sprzedawcy, dla którego tworzony jest raport.
     * @param dateFrom data początkowa zakresu raportu (włącznie).
     * @param dateTo data końcowa zakresu raportu (włącznie).
     * @param category opcjonalny filtr kategorii (nazwa kategorii).
     * @return odpowiedź HTTP zawierająca plik PDF jako tablicę bajtów.
     */
    @GetMapping("/seller/{sellerId}/sales")
    public ResponseEntity<byte[]> getSellerSalesReport(
            @PathVariable Long sellerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String category
    ) {
        byte[] pdfBytes = reportService.generateSellerSalesReport(sellerId, dateFrom, dateTo, category);
        return buildPdfResponse(pdfBytes, "raport_sprzedazy_sprzedawcy_" + sellerId + ".pdf");
    }

    // ==================== RAPORTY ADMINA ====================

    /**
     * Pobiera raport aktywności użytkowników w formacie PDF (dla admina).
     *
     * @param dateFrom data początkowa zakresu raportu (włącznie).
     * @param dateTo data końcowa zakresu raportu (włącznie).
     * @param role opcjonalny filtr roli użytkownika.
     * @return odpowiedź HTTP zawierająca plik PDF jako tablicę bajtów.
     */
    @GetMapping("/admin/user-activity")
    public ResponseEntity<byte[]> getAdminUserActivityReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String role
    ) {
        byte[] pdfBytes = reportService.generateAdminUserActivityReport(dateFrom, dateTo, role);
        return buildPdfResponse(pdfBytes, "raport_aktywnosci_uzytkownikow.pdf");
    }

    /**
     * Pobiera raport prowizji systemowych w formacie PDF (dla admina).
     *
     * @param dateFrom data początkowa zakresu raportu (włącznie).
     * @param dateTo data końcowa zakresu raportu (włącznie).
     * @param category opcjonalny filtr kategorii (nazwa kategorii).
     * @return odpowiedź HTTP zawierająca plik PDF jako tablicę bajtów.
     */
    @GetMapping("/admin/commissions")
    public ResponseEntity<byte[]> getAdminCommissionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String category
    ) {
        byte[] pdfBytes = reportService.generateSystemCommissionReport(dateFrom, dateTo, category);
        return buildPdfResponse(pdfBytes, "raport_prowizji_systemowych.pdf");
    }

    // ==================== POMOCNICZE ====================

    /**
     * Buduje odpowiedź HTTP dla pliku PDF z poprawnymi nagłówkami.
     *
     * @param pdfBytes zawartość PDF jako tablica bajtów.
     * @param filename nazwa pliku wyświetlana przy pobieraniu.
     * @return odpowiedź HTTP z nagłówkami i treścią PDF.
     */
    private ResponseEntity<byte[]> buildPdfResponse(byte[] pdfBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(pdfBytes.length);
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
