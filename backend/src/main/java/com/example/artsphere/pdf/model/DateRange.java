package com.example.artsphere.pdf.model;

import java.time.LocalDate;

/**
 * Zakres dat używany w raportach PDF.
 *
 * @param from data początkowa zakresu (włącznie).
 * @param to data końcowa zakresu (włącznie).
 */
public record DateRange(LocalDate from, LocalDate to) {
}
