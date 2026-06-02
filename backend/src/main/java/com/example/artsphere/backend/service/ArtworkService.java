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

/**
 * Serwis logiki biznesowej dla dzieł sztuki.
 *
 * @author Gemini CLI
 */
@Service
public class ArtworkService {
    /**
     * Konstruktor domyślny.
     */
    public ArtworkService() {}

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Zwraca listę dzieł przypisanych do sprzedawcy.
     *
     * @param userId identyfikator sprzedawcy.
     * @return lista dzieł sprzedawcy w formacie {@link ArtworkResponse}.
     */
    public List<ArtworkResponse> getSellerArtworks(Long userId) {
        List<Artwork> artworks = artworkRepository.findByUserId(userId);
        return artworks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * Zwraca wszystkie dostępne dzieła (niesprzedane).
     *
     * @return lista dostępnych dzieł w formacie {@link ArtworkResponse}.
     */
    public List<ArtworkResponse> getAllAvailableArtworks() {
        List<Artwork> artworks = artworkRepository.findByIsSoldFalse();
        return artworks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * Zwraca wszystkie dzieła w systemie (w tym sprzedane).
     *
     * @return lista wszystkich dzieł w formacie {@link ArtworkResponse}.
     */
    public List<ArtworkResponse> getAllArtworks() {
        return artworkRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera szczegóły dzieła na podstawie identyfikatora.
     *
     * @param artworkId identyfikator dzieła.
     * @return szczegóły dzieła w formacie {@link ArtworkResponse}.
     * @throws RuntimeException gdy dzieło nie istnieje.
     */
    public ArtworkResponse getArtworkById(Long artworkId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));
        return convertToResponse(artwork);
    }

    /**
     * Pobiera listę kategorii dzieł wraz z podstawowymi statystykami.
     *
     * @return lista kategorii w formacie {@link CategoryResponse}.
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tworzy nowe dzieło sztuki przypisane do sprzedawcy.
     *
     * @param userId identyfikator sprzedawcy (właściciela dzieła).
     * @param request dane nowego dzieła przekazane przez użytkownika.
     * @return utworzone dzieło w formacie {@link ArtworkResponse}.
     * @throws RuntimeException gdy użytkownik lub kategoria nie istnieją.
     */
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

        if (request.getIsPriceless() != null && request.getIsPriceless()) {
            artwork.setPrice(null);
        } else {
            artwork.setPrice(request.getPrice());
        }

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

    /**
     * Aktualizuje istniejące dzieło.
     * Gdy userId jest przekazane, sprawdza właściciela; gdy null, zakłada tryb admina.
     *
     * @param artworkId identyfikator dzieła do aktualizacji.
     * @param userId identyfikator użytkownika wykonującego operację lub null dla admina.
     * @param request nowe dane dzieła.
     * @return zaktualizowane dzieło w formacie {@link ArtworkResponse}.
     * @throws RuntimeException gdy dzieło nie istnieje lub brak uprawnień.
     */
    public ArtworkResponse updateArtwork(Long artworkId, Long userId, ArtworkRequest request) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));

        // If userId is provided, check ownership. If null (admin), bypass.
        if (userId != null && !artwork.getUser().getId().equals(userId)) {
            throw new RuntimeException("Brak uprawnień do edycji tego dzieła");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Kategoria nie znaleziona"));
        }

        artwork.setTitle(request.getTitle());
        artwork.setDescription(request.getDescription());

        if (request.getIsPriceless() != null && request.getIsPriceless()) {
            artwork.setPrice(null);
        } else {
            artwork.setPrice(request.getPrice());
        }

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

    /**
     * Usuwa dzieło z systemu po weryfikacji właściciela.
     *
     * @param artworkId identyfikator dzieła do usunięcia.
     * @param userId identyfikator użytkownika wykonującego operację lub null dla admina.
     * @throws RuntimeException gdy dzieło nie istnieje lub brak uprawnień.
     */
    public void deleteArtwork(Long artworkId, Long userId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));

        if (userId != null && !artwork.getUser().getId().equals(userId)) {
            throw new RuntimeException("Brak uprawnień do usunięcia tego dzieła");
        }

        artworkRepository.delete(artwork);
    }

    /**
     * Mapuje encję {@link Artwork} do obiektu odpowiedzi.
     *
     * @param artwork encja dzieła pobrana z bazy danych.
     * @return dzieło w formacie {@link ArtworkResponse}.
     */
    private ArtworkResponse convertToResponse(Artwork artwork) {
        return new ArtworkResponse(
                artwork.getId(),
                artwork.getTitle(),
                artwork.getDescription(),
                artwork.getPrice(),
                artwork.getIsPriceless() != null && artwork.getIsPriceless(),
                artwork.getArtist(),
                artwork.getImagePath(),
                artwork.getWidth(),
                artwork.getHeight(),
                artwork.getDepth(),
                artwork.getUser() != null ? artwork.getUser().getId() : 0L,
                artwork.getUser() != null ? artwork.getUser().getUsername() : "Nieznany",
                artwork.getCategory() != null ? artwork.getCategory().getId() : null,
                artwork.getCategory() != null ? artwork.getCategory().getName() : null,
                artwork.getIsSold() != null && artwork.getIsSold(),
                artwork.getStatus(),
                artwork.getCreatedAt(),
                null
        );
    }

    /**
     * Mapuje encję {@link Category} do obiektu odpowiedzi wraz z licznikami dzieł.
     *
     * @param category encja kategorii pobrana z bazy danych.
     * @return kategoria w formacie {@link CategoryResponse}.
     */
    private CategoryResponse convertToCategoryResponse(Category category) {
        int artworkCount = artworkRepository.countByCategoryId(category.getId());
        int soldCount = artworkRepository.countByCategoryIdAndIsSoldTrue(category.getId());
        
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParent() != null ? category.getParent().getName() : null,
                category.getIsActive(),
                artworkCount,
                soldCount,
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDisplayOrder(),
                category.getIconName(),
                category.getColor()
        );
    }

    /**
     * Oznacza dzieło jako sprzedane i aktualizuje jego status.
     *
     * @param artworkId identyfikator dzieła do oznaczenia.
     * @throws RuntimeException gdy dzieło nie istnieje.
     */
    public void markAsSold(Long artworkId) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new RuntimeException("Dzieło nie znalezione"));
        artwork.setIsSold(true);
        artwork.setStatus("SOLD");
        artworkRepository.save(artwork);
    }
}
