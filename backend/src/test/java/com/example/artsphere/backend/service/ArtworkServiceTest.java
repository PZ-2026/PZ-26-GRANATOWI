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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtworkServiceTest {

    @Mock
    private ArtworkRepository artworkRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArtworkService artworkService;

    private User testUser;
    private Category testCategory;
    private Artwork testArtwork;
    private ArtworkRequest artworkRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("artist_jan");

        testCategory = new Category();
        testCategory.setId(1); // Typ Integer zgodnie z Twoim modelem
        testCategory.setName("Malarstwo");

        testArtwork = new Artwork();
        testArtwork.setId(1L);
        testArtwork.setTitle("Zachód słońca");
        testArtwork.setUser(testUser);
        testArtwork.setCategory(testCategory);
        testArtwork.setIsSold(false);
        testArtwork.setStatus("AVAILABLE");

        artworkRequest = new ArtworkRequest();
        artworkRequest.setTitle("Nowy obraz");
        artworkRequest.setCategoryId(1);
        artworkRequest.setIsPriceless(false);
        artworkRequest.setPrice(BigDecimal.valueOf(100.0));
    }

    @Test
    @DisplayName("Should return all artworks for specific seller")
    void shouldReturnAllArtworksForSpecificSeller() {
        when(artworkRepository.findByUserId(1L)).thenReturn(Arrays.asList(testArtwork));

        List<ArtworkResponse> result = artworkService.getSellerArtworks(1L);

        assertEquals(1, result.size());
        verify(artworkRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should return only unsold artworks")
    void shouldReturnOnlyUnsoldArtworks() {
        when(artworkRepository.findByIsSoldFalse()).thenReturn(Collections.singletonList(testArtwork));

        List<ArtworkResponse> result = artworkService.getAllAvailableArtworks();

        assertFalse(result.get(0).getIsSold());
        verify(artworkRepository).findByIsSoldFalse();
    }

    @Test
    @DisplayName("Should throw exception when artwork not found by ID")
    void shouldThrowExceptionWhenArtworkNotFoundById() {
        when(artworkRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> artworkService.getArtworkById(99L));
    }

    @Test
    @DisplayName("Should return all categories with counts from repository")
    void shouldReturnAllCategoriesWithCounts() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(testCategory));
        when(artworkRepository.countByCategoryId(1)).thenReturn(10);
        when(artworkRepository.countByCategoryIdAndIsSoldTrue(1)).thenReturn(3);

        // Act
        List<CategoryResponse> result = artworkService.getAllCategories();

        // Assert
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getArtworkCount());

        assertEquals(3, result.get(0).getSoldArtworkCount());
    }

    @Test
    @DisplayName("Should create new artwork successfully")
    void shouldCreateNewArtworkSuccessfully() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(artworkRepository.save(any(Artwork.class))).thenReturn(testArtwork);

        // Act
        ArtworkResponse response = artworkService.createArtwork(1L, artworkRequest);

        // Assert
        assertNotNull(response);
        verify(artworkRepository).save(any(Artwork.class));
    }

    @Test
    @DisplayName("Should set price to null when artwork is priceless during creation")
    void shouldSetPriceToNullWhenIsPriceless() {
        // Arrange
        artworkRequest.setIsPriceless(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        // Używamy ArgumentCaptor lub sprawdzamy obiekt przekazany do save
        when(artworkRepository.save(any(Artwork.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        ArtworkResponse response = artworkService.createArtwork(1L, artworkRequest);

        // Assert
        assertTrue(response.getIsPriceless());
        assertNull(response.getPrice());
    }

    @Test
    @DisplayName("Should throw exception when non-owner tries to update artwork")
    void shouldThrowExceptionWhenNotOwnerTriesToUpdate() {
        // Arrange
        User anotherUser = new User();
        anotherUser.setId(2L);
        testArtwork.setUser(anotherUser); // Właścicielem jest ID 2

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));

        // Act & Assert (Próbujemy edytować jako użytkownik ID 1)
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> artworkService.updateArtwork(1L, 1L, artworkRequest));

        assertEquals("Brak uprawnień do edycji tego dzieła", ex.getMessage());
    }

    @Test
    @DisplayName("Should allow admin to update artwork (userId is null)")
    void shouldAllowAdminToUpdate() {
        // Arrange
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(artworkRepository.save(any(Artwork.class))).thenReturn(testArtwork);

        // Act
        ArtworkResponse response = artworkService.updateArtwork(1L, null, artworkRequest);

        // Assert
        assertNotNull(response);
        verify(artworkRepository).save(testArtwork);
    }

    @Test
    @DisplayName("Should mark artwork as sold")
    void shouldMarkAsSold() {
        // Arrange
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));

        // Act
        artworkService.markAsSold(1L);

        // Assert
        assertTrue(testArtwork.getIsSold());
        assertEquals("SOLD", testArtwork.getStatus());
        verify(artworkRepository).save(testArtwork);
    }
}