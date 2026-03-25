package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.ArtworkRequest;
import com.example.artsphere.backend.dto.ArtworkResponse;
import com.example.artsphere.backend.dto.CategoryResponse;
import com.example.artsphere.backend.model.Artwork;
import com.example.artsphere.backend.model.Category;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.ArtworkRepository;
import com.example.artsphere.backend.repository.CategoryRepository;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // Pobierz wszystkie dzieła sprzedawcy
    public List<ArtworkResponse> getSellerArtworks(Long userId) {
        List<Artwork> artworks = artworkRepository.findByUserId(userId);
        return artworks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Pobierz wszystkie kategorie
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    // Dodaj nowe dzieło
    public ArtworkResponse createArtwork(Long userId, ArtworkRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategoria nie znaleziona"));
        }

        Artwork artwork = new Artwork();
        artwork.setTitle(request.getTitle());
        artwork.setDescription(request.getDescription());
        artwork.setPrice(request.getIsPriceless() ? null : request.getPrice());
        artwork.setIsPriceless(request.getIsPriceless() != null ? request.getIsPriceless() : false);
        artwork.setArtist(request.getArtist());
        artwork.setImagePath(request.getImagePath());
        artwork.setWidth(request.getWidth());
        artwork.setHeight(request.getHeight());
        artwork.setDepth(request.getDepth());
        artwork.setUser(user);
        artwork.setCategory(category);
        artwork.setIsSold(false);
        artwork.setStatus("AVAILABLE");

        Artwork saved = artworkRepository.save(artwork);
        return convertToResponse(saved);
    }

    // Zaktualizuj dzieło
    public ArtworkResponse updateArtwork(Long artworkId, Long userId, ArtworkRequest request) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));

        if (!artwork.getUser().getId().equals(userId)) {
            throw new RuntimeException("Brak uprawnień do edycji tego dzieła");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategoria nie znaleziona"));
        }

        artwork.setTitle(request.getTitle());
        artwork.setDescription(request.getDescription());
        artwork.setPrice(request.getIsPriceless() ? null : request.getPrice());
        artwork.setIsPriceless(request.getIsPriceless() != null ? request.getIsPriceless() : false);
        artwork.setArtist(request.getArtist());
        artwork.setImagePath(request.getImagePath());
        artwork.setWidth(request.getWidth());
        artwork.setHeight(request.getHeight());
        artwork.setDepth(request.getDepth());
        artwork.setCategory(category);

        Artwork updated = artworkRepository.save(artwork);
        return convertToResponse(updated);
    }

    // Usuń dzieło
    public void deleteArtwork(Long artworkId, Long userId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));

        if (!artwork.getUser().getId().equals(userId)) {
            throw new RuntimeException("Brak uprawnień do usunięcia tego dzieła");
        }

        artworkRepository.delete(artwork);
    }

    // Pobierz dzieło po ID
    public ArtworkResponse getArtworkById(Long artworkId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));
        return convertToResponse(artwork);
    }

    // Pobierz wszystkie dostępne dzieła (dla kupujących)
    public List<ArtworkResponse> getAllAvailableArtworks() {
        List<Artwork> artworks = artworkRepository.findByIsSoldFalse();
        return artworks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ArtworkResponse convertToResponse(Artwork artwork) {
        return new ArtworkResponse(
                artwork.getId(),
                artwork.getTitle(),
                artwork.getDescription(),
                artwork.getPrice(),
                artwork.getIsPriceless(),
                artwork.getArtist(),
                artwork.getImagePath(),
                artwork.getWidth(),
                artwork.getHeight(),
                artwork.getDepth(),
                artwork.getUser().getId(),
                artwork.getUser().getUsername(),
                artwork.getCategory() != null ? artwork.getCategory().getId() : null,
                artwork.getCategory() != null ? artwork.getCategory().getName() : null,
                artwork.getIsSold(),
                artwork.getStatus(),
                artwork.getCreatedAt(),
                artwork.getUpdatedAt()
        );
    }
}