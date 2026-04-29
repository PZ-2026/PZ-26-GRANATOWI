package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.ArtistDto;
import com.example.artsphere.backend.dto.DonationHistoryResponse;
import com.example.artsphere.backend.dto.DonationRequest;
import com.example.artsphere.backend.model.Donation;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.DonationRepository;
import com.example.artsphere.backend.repository.UserRepository;
import com.example.artsphere.backend.repository.WalletTransactionRepository;
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
class SupportControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @InjectMocks
    private SupportController supportController;

    private User testClient;
    private User testArtist;
    private Donation testDonation;

    @BeforeEach
    void setUp() {
        testClient = new User();
        testClient.setId(1L);
        testClient.setUsername("buyer_jan");
        testClient.setBalance(BigDecimal.valueOf(500.00));

        testArtist = new User();
        testArtist.setId(2L);
        testArtist.setUsername("artist_anna");
        testArtist.setFirstName("Anna");
        testArtist.setLastName("Kowalska");
        testArtist.setBalance(BigDecimal.valueOf(1000.00));

        testDonation = new Donation();
        testDonation.setId(1L);
        testDonation.setClient(testClient);
        testDonation.setSeller(testArtist);
        testDonation.setAmount(BigDecimal.valueOf(50.00));
    }

    @Test
    @DisplayName("Should return list of artists")
    void shouldReturnListOfArtists() {
        // Arrange
        when(userRepository.findByRole("ARTIST")).thenReturn(Collections.singletonList(testArtist));

        // Act
        ResponseEntity<List<ArtistDto>> response = supportController.getArtists();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        List<ArtistDto> artists = response.getBody();
        assertEquals(1, artists.size());
        assertEquals("artist_anna", artists.get(0).getUsername());
        assertEquals("Anna", artists.get(0).getFirstName());
    }

    @Test
    @DisplayName("Should process donation successfully")
    void shouldProcessDonationSuccessfully() {
        // Arrange
        DonationRequest request = new DonationRequest();
        request.setClientId(1L);
        request.setSellerId(2L);
        request.setAmount(50.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testArtist));

        // Act
        ResponseEntity<?> response = supportController.donate(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(450.0, testClient.getBalance().doubleValue()); // 500 - 50
        assertEquals(1050.0, testArtist.getBalance().doubleValue()); // 1000 + 50
        verify(donationRepository, times(1)).save(any(Donation.class));
        verify(walletTransactionRepository, times(2)).save(any()); // 2 transakcje
    }

    @Test
    @DisplayName("Should return error when insufficient funds")
    void shouldReturnErrorWhenInsufficientFunds() {
        // Arrange
        testClient.setBalance(BigDecimal.valueOf(20.00));
        DonationRequest request = new DonationRequest();
        request.setClientId(1L);
        request.setSellerId(2L);
        request.setAmount(50.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testArtist));

        // Act
        ResponseEntity<?> response = supportController.donate(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Brak wystarczających środków", body.get("error"));
        verify(donationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return donation history for user")
    void shouldReturnDonationHistoryForUser() {
        // Arrange
        when(donationRepository.findByClientIdOrderByIdDesc(1L))
                .thenReturn(Collections.singletonList(testDonation));

        // Act
        ResponseEntity<List<DonationHistoryResponse>> response = supportController.getHistory(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        List<DonationHistoryResponse> history = response.getBody();
        assertEquals(1, history.size());
        assertEquals("artist_anna", history.get(0).getArtistName());
        assertEquals(50.0, history.get(0).getAmount());
    }
}