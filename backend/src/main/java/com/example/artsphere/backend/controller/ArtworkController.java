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

    @GetMapping("/seller/{userId}")
    public ResponseEntity<List<ArtworkResponse>> getSellerArtworks(@PathVariable Long userId) {
        return ResponseEntity.ok(artworkService.getSellerArtworks(userId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(artworkService.getAllCategories());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ArtworkResponse>> getAllAvailableArtworks() {
        return ResponseEntity.ok(artworkService.getAllAvailableArtworks());
    }

    @GetMapping("/{artworkId}")
    public ResponseEntity<ArtworkResponse> getArtworkById(@PathVariable Long artworkId) {
        return ResponseEntity.ok(artworkService.getArtworkById(artworkId));
    }

    @PostMapping("/seller/{userId}")
    public ResponseEntity<?> createArtwork(@PathVariable Long userId, @RequestBody ArtworkRequest request) {
        return ResponseEntity.ok(artworkService.createArtwork(userId, request));
    }

    @PutMapping("/{artworkId}/seller/{userId}")
    public ResponseEntity<?> updateArtwork(@PathVariable Long artworkId, @PathVariable Long userId, @RequestBody ArtworkRequest request) {
        return ResponseEntity.ok(artworkService.updateArtwork(artworkId, userId, request));
    }

    @DeleteMapping("/{artworkId}/seller/{userId}")
    public ResponseEntity<?> deleteArtwork(@PathVariable Long artworkId, @PathVariable Long userId) {
        artworkService.deleteArtwork(artworkId, userId);
        return ResponseEntity.ok("Dzieło usunięte pomyślnie");
    }

    // NOWY ENDPOINT: Oznaczanie dzieła jako sprzedane (po zakupie w koszyku)
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