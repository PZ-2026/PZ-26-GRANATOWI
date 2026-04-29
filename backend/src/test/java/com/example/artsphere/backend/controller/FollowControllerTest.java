package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.model.Artwork;
import com.example.artsphere.backend.model.SellerUserFollow;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.ArtworkRepository;
import com.example.artsphere.backend.repository.SellerUserFollowRepository;
import com.example.artsphere.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private SellerUserFollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtworkRepository artworkRepository;

    @InjectMocks
    private FollowController followController;

    private User testUser;
    private User testSeller;
    private SellerUserFollow testFollow;
    private Artwork testArtwork;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("buyer_jan");
        testUser.setEmail("buyer@example.com");
        testUser.setRole("BUYER");

        testSeller = new User();
        testSeller.setId(2L);
        testSeller.setUsername("artist_anna");
        testSeller.setEmail("artist@example.com");
        testSeller.setRole("ARTIST");
        testSeller.setFirstName("Anna");
        testSeller.setLastName("Kowalska");

        testFollow = new SellerUserFollow();
        testFollow.setId(1L);
        testFollow.setUser(testUser);
        testFollow.setSeller(testSeller);

        testArtwork = new Artwork();
        testArtwork.setId(1L);
        testArtwork.setTitle("Obraz artysty");
        testArtwork.setUser(testSeller);
        testArtwork.setStatus("AVAILABLE");
    }

    @Test
    @DisplayName("Should follow seller successfully")
    void shouldFollowSellerSuccessfully() {
        // Arrange
        when(followRepository.findByUserIdAndSellerId(1L, 2L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testSeller));

        // Act
        ResponseEntity<?> response = followController.follow(1L, 2L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Zaobserwowano", body.get("message"));
        verify(followRepository, times(1)).save(any(SellerUserFollow.class));
    }

    @Test
    @DisplayName("Should unfollow seller successfully")
    void shouldUnfollowSellerSuccessfully() {
        // Arrange
        when(followRepository.findByUserIdAndSellerId(1L, 2L)).thenReturn(Optional.of(testFollow));

        // Act
        ResponseEntity<?> response = followController.unfollow(1L, 2L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Odobserwowano", body.get("message"));
        verify(followRepository, times(1)).delete(testFollow);
    }

    @Test
    @DisplayName("Should check if user is following seller")
    void shouldCheckIfUserIsFollowingSeller() {
        // Arrange
        when(followRepository.findByUserIdAndSellerId(1L, 2L)).thenReturn(Optional.of(testFollow));

        // Act
        ResponseEntity<?> response = followController.isFollowing(1L, 2L);

        // Assert
        Map<String, Boolean> body = (Map<String, Boolean>) response.getBody();
        assertTrue(body.get("isFollowing"));
    }

    @Test
    @DisplayName("Should return artworks from followed sellers")
    void shouldReturnArtworksFromFollowedSellers() {
        // Arrange
        when(followRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testFollow));
        when(artworkRepository.findAll()).thenReturn(Collections.singletonList(testArtwork));

        // Act
        ResponseEntity<?> response = followController.getFollowedArtworks(1L);

        // Assert
        List<Artwork> artworks = (List<Artwork>) response.getBody();
        assertEquals(1, artworks.size());
        assertEquals("Obraz artysty", artworks.get(0).getTitle());
    }
}