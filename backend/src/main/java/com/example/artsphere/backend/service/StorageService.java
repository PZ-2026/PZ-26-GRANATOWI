package com.example.artsphere.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Serwis odpowiedzialny za trwałe przechowywanie plików (obrazów) na serwerze.
 * Zarządza fizycznym zapisem danych w systemie plików w folderze "uploads".
 *
 * @author Gemini CLI
 */
@Service
public class StorageService {

    /**
     * Konstruktor domyślny.
     */
    public StorageService() {}

    /** Ścieżka bazowa dla przechowywanych plików. */
    private final Path root = Paths.get("uploads");

    /**
     * Inicjalizuje infrastrukturę do przechowywania plików.
     * Tworzy folder docelowy "uploads", jeśli nie został jeszcze utworzony.
     * 
     * @throws RuntimeException jeśli wystąpi błąd podczas tworzenia katalogu.
     */
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się zainicjalizować folderu na pliki!");
        }
    }

    /**
     * Zapisuje przesłany przez użytkownika plik na dysku serwera.
     * Generuje unikalną nazwę pliku (UUID), aby zapobiec nadpisaniu istniejących danych.
     * Automatycznie wykrywa i zachowuje oryginalne rozszerzenie pliku.
     *
     * @param file obiekt MultipartFile otrzymany z kontrolera REST.
     * @return relatywna ścieżka do zapisanego pliku (np. "uploads/uuid.jpg").
     * @throws RuntimeException jeśli plik jest pusty lub wystąpi błąd wejścia/wyjścia podczas zapisu.
     */
    public String store(MultipartFile file) {
        try {
            init();
            if (file.isEmpty()) {
                throw new RuntimeException("Nie można zapisać pustego pliku.");
            }
            
            String extension = "";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String filename = UUID.randomUUID().toString() + extension;
            Files.copy(file.getInputStream(), this.root.resolve(filename));
            
            return "uploads/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas zapisywania pliku: " + e.getMessage());
        }
    }
}
