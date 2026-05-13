package com.example.artsphere.backend.logging;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthFileLogger {

    private static final Path LOG_PATH = Paths.get("logs", "auth-events.log");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public synchronized void logLogin(String email, boolean success, String message) {
        String status = success ? "SUCCESS" : "FAILURE";
        String line = String.format("%s | LOGIN | %s | email=%s | %s\n", LocalDateTime.now().format(FORMATTER), status, safe(email), safe(message));
        writeLine(line);
    }

    public synchronized void logRegister(String username, String email, boolean success, String message) {
        String status = success ? "SUCCESS" : "FAILURE";
        String line = String.format("%s | REGISTER | %s | username=%s | email=%s | %s\n", LocalDateTime.now().format(FORMATTER), status, safe(username), safe(email), safe(message));
        writeLine(line);
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll("\n", " ").trim();
    }

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
