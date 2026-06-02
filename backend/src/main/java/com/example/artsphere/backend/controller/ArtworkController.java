package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.ArtworkRequest;
import com.example.artsphere.backend.dto.ArtworkResponse;
import com.example.artsphere.backend.dto.CategoryResponse;
import com.example.artsphere.backend.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler REST do obsługi dzieł sztuki.
 */
@RestController
@RequestMapping("/api/artworks")
@CrossOrigin(origins = "*")
public class ArtworkController {
    /**
     * Konstruktor domyślny.
     */
    public ArtworkController() {}

    @Autowired
    private ArtworkService artworkService;

    /**
     * Endpoint zwracający listę dzieł sprzedawcy.
     *
     * @param userId identyfikator sprzedawcy w ścieżce URL.
     * @return lista dzieł sprzedawcy.
     */
    @GetMapping("/seller/{userId}")
    public ResponseEntity<List<ArtworkResponse>> getSellerArtworks(@PathVariable Long userId) {
        return ResponseEntity.ok(artworkService.getSellerArtworks(userId));
    }

    /**
     * Endpoint zwracający listę kategorii dzieł.
     *
     * @return lista kategorii z podstawowymi statystykami.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(artworkService.getAllCategories());
    }

    /**
     * Endpoint zwracający wszystkie dostępne dzieła (niesprzedane).
     *
     * @return lista dostępnych dzieł.
     */
    @GetMapping("/available")
    public ResponseEntity<List<ArtworkResponse>> getAllAvailableArtworks() {
        return ResponseEntity.ok(artworkService.getAllAvailableArtworks());
    }

    /**
     * Endpoint zwracający szczegóły dzieła.
     *
     * @param artworkId identyfikator dzieła w ścieżce URL.
     * @return szczegóły dzieła.
     */
    @GetMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> getArtworkById(@PathVariable Long artworkId) {
        return ResponseEntity.ok(artworkService.getArtworkById(artworkId));
    }

    /**
     * Endpoint tworzący nowe dzieło dla sprzedawcy.
     *
     * @param userId identyfikator sprzedawcy w ścieżce URL.
     * @param request dane dzieła przekazane w ciele żądania.
     * @return utworzone dzieło lub błąd walidacji.
     */
    @PostMapping("/seller/{userId}")
    public ResponseEntity<?> createArtwork(@PathVariable Long userId, @RequestBody ArtworkRequest request) {
        return ResponseEntity.ok(artworkService.createArtwork(userId, request));
    }

    /**
     * Endpoint aktualizujący dzieło należące do sprzedawcy.
     *
     * @param artworkId identyfikator dzieła w ścieżce URL.
     * @param userId identyfikator sprzedawcy w ścieżce URL.
     * @param request nowe dane dzieła przekazane w ciele żądania.
     * @return zaktualizowane dzieło lub błąd walidacji.
     */
    @PutMapping("/{artworkId}/seller/{userId}")
    public ResponseEntity<?> updateArtwork(@PathVariable Long artworkId, @PathVariable Long userId, @RequestBody ArtworkRequest request) {
        return ResponseEntity.ok(artworkService.updateArtwork(artworkId, userId, request));
    }

    /**
     * Endpoint usuwający dzieło należące do sprzedawcy.
     *
     * @param artworkId identyfikator dzieła w ścieżce URL.
     * @param userId identyfikator sprzedawcy w ścieżce URL.
     * @return komunikat potwierdzający usunięcie.
     */
    @DeleteMapping("/{artworkId}/seller/{userId}")
    public ResponseEntity<?> deleteArtwork(@PathVariable Long artworkId, @PathVariable Long userId) {
        artworkService.deleteArtwork(artworkId, userId);
        return ResponseEntity.ok("Dzieło usunięte pomyślnie");
    }

    // NOWY ENDPOINT: Oznaczanie dzieła jako sprzedane (po zakupie w koszyku)
    /**
     * Endpoint oznaczający dzieło jako sprzedane.
     *
     * @param artworkId identyfikator dzieła w ścieżce URL.
     * @return komunikat sukcesu lub błąd walidacji.
     */
    @PutMapping("/{artworkId}/mark-sold")
    public ResponseEntity<?> markAsSold(@PathVariable Long artworkId) {
        try {
            artworkService.markAsSold(artworkId);
            return ResponseEntity.ok("Dzieło zostało oznaczone jako sprzedane");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}