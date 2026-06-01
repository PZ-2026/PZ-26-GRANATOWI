package com.example.artsphere.backend.logging;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger zapisujący zdarzenia uwierzytelniania do pliku.
 */
@Service
public class AuthFileLogger {

    private static final Path LOG_PATH = Paths.get("logs", "auth-events.log");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Zapisuje zdarzenie logowania do pliku logów.
     *
     * @param email adres e-mail użyty do logowania.
     * @param success flaga informująca, czy logowanie zakończyło się sukcesem.
     * @param message dodatkowy komunikat opisujący wynik operacji.
     */
    public synchronized void logLogin(String email, boolean success, String message) {
        String status = success ? "SUCCESS" : "FAILURE";
        String line = String.format("%s | LOGIN | %s | email=%s | %s\n", LocalDateTime.now().format(FORMATTER), status, safe(email), safe(message));
        writeLine(line);
    }

    /**
     * Zapisuje zdarzenie rejestracji do pliku logów.
     *
     * @param username nazwa użytkownika użyta przy rejestracji.
     * @param email adres e-mail użyty przy rejestracji.
     * @param success flaga informująca, czy rejestracja zakończyła się sukcesem.
     * @param message dodatkowy komunikat opisujący wynik operacji.
     */
    public synchronized void logRegister(String username, String email, boolean success, String message) {
        String status = success ? "SUCCESS" : "FAILURE";
        String line = String.format("%s | REGISTER | %s | username=%s | email=%s | %s\n", LocalDateTime.now().format(FORMATTER), status, safe(username), safe(email), safe(message));
        writeLine(line);
    }

    /**
     * Oczyszcza tekst na potrzeby logów (usuwa znaki nowej linii i trimuje).
     *
     * @param s tekst wejściowy.
     * @return oczyszczony tekst lub pusty łańcuch, gdy wejście jest null.
     */
    private String safe(String s) {
        return s == null ? "" : s.replaceAll("\n", " ").trim();
    }

    /**
     * Zapisuje pojedynczą linię do pliku logów, tworząc katalog jeśli nie istnieje.
     *
     * @param line linia tekstu do zapisu.
     */
    private void writeLine(String line) {
        try {
            Path dir = LOG_PATH.getParent();
            if (dir != null && !Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Files.writeString(LOG_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Logging to file failed — swallow to avoid affecting application flow
            System.err.println("Failed to write auth log: " + e.getMessage());
        }
    }
}
