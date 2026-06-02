package com.example.artsphere.pdf.model;

import java.time.LocalDate;

/**
 * Zakres dat używany w raportach PDF.
 *
 * @param from data początkowa zakresu (włącznie).
 * @param to data końcowa zakresu (włącznie).
 * @author Gemini CLI
 */
public record DateRange(LocalDate from, LocalDate to) {
    /**
     * Konstruktor domyślny.
     */
    public DateRange() {
        this(null, null);
    }
}
