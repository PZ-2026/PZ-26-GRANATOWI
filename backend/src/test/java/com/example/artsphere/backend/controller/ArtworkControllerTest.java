package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.ArtworkRequest;
import com.example.artsphere.backend.dto.ArtworkResponse;
import com.example.artsphere.backend.dto.CategoryResponse;
import com.example.artsphere.backend.service.ArtworkService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtworkControllerTest {

    @Mock
    private ArtworkService artworkService;

    @InjectMocks
    private ArtworkController artworkController;

    @Test
    void getAllCategoriesShouldReturnOk() {
        CategoryResponse category = new CategoryResponse(1, "Malarstwo", "desc", "malarstwo", null, null, true, 3, 1, null, null, 0, null, null);
        when(artworkService.getAllCategories()).thenReturn(List.of(category));

        ResponseEntity<List<CategoryResponse>> result = artworkController.getAllCategories();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("Malarstwo", result.getBody().getFirst().getName());
    }

    @Test
    void createArtworkShouldReturnSavedArtwork() {
        ArtworkRequest request = new ArtworkRequest("Obraz", "Opis", BigDecimal.TEN, false, "Artysta", null, null, null, null, 1);
        ArtworkResponse response = new ArtworkResponse();
        response.setId(11L);
        response.setTitle("Obraz");

        when(artworkService.createArtwork(5L, request)).thenReturn(response);

        ResponseEntity<?> result = artworkController.createArtwork(5L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(artworkService).createArtwork(5L, request);
    }

    @Test
    void markAsSoldShouldReturnBadRequestWhenServiceThrows() {
        doThrow(new RuntimeException("Brak dziela")).when(artworkService).markAsSold(7L);

        ResponseEntity<?> result = artworkController.markAsSold(7L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Brak dziela", result.getBody());
    }

    @Test
    void deleteArtworkShouldReturnSuccessMessage() {
        ResponseEntity<?> result = artworkController.deleteArtwork(7L, 5L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Dzieło usunięte pomyślnie", result.getBody());
        verify(artworkService).deleteArtwork(7L, 5L);
    }
}
