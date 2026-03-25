package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.ArtworkRequest;
import com.example.artsphere.backend.dto.ArtworkResponse;
import com.example.artsphere.backend.dto.CategoryResponse;
import com.example.artsphere.backend.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
@CrossOrigin(origins = "*")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    // Pobierz dzieła sprzedawcy
    @GetMapping("/seller/{userId}")
    public ResponseEntity<List<ArtworkResponse>> getSellerArtworks(@PathVariable Long userId) {
        try {
            List<ArtworkResponse> artworks = artworkService.getSellerArtworks(userId);
            return ResponseEntity.ok(artworks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Pobierz wszystkie kategorie
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        try {
            List<CategoryResponse> categories = artworkService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Pobierz wszystkie dostępne dzieła (dla kupujących)
    @GetMapping("/available")
    public ResponseEntity<List<ArtworkResponse>> getAllAvailableArtworks() {
        try {
            List<ArtworkResponse> artworks = artworkService.getAllAvailableArtworks();
            return ResponseEntity.ok(artworks);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Pobierz dzieło po ID
    @GetMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> getArtworkById(@PathVariable Long artworkId) {
        try {
            ArtworkResponse artwork = artworkService.getArtworkById(artworkId);
            return ResponseEntity.ok(artwork);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Dodaj nowe dzieło
    @PostMapping("/seller/{userId}")
    public ResponseEntity<?> createArtwork(@PathVariable Long userId, @RequestBody ArtworkRequest request) {
        try {
            ArtworkResponse artwork = artworkService.createArtwork(userId, request);
            return ResponseEntity.ok(artwork);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Zaktualizuj dzieło
    @PutMapping("/{artworkId}/seller/{userId}")
    public ResponseEntity<?> updateArtwork(
            @PathVariable Long artworkId,
            @PathVariable Long userId,
            @RequestBody ArtworkRequest request) {
        try {
            ArtworkResponse artwork = artworkService.updateArtwork(artworkId, userId, request);
            return ResponseEntity.ok(artwork);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Usuń dzieło
    @DeleteMapping("/{artworkId}/seller/{userId}")
    public ResponseEntity<?> deleteArtwork(@PathVariable Long artworkId, @PathVariable Long userId) {
        try {
            artworkService.deleteArtwork(artworkId, userId);
            return ResponseEntity.ok("Dzieło usunięte pomyślnie");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}