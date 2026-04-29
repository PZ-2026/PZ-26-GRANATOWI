package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.*;
import com.example.artsphere.backend.model.*;
import com.example.artsphere.backend.repository.*;
import com.example.artsphere.backend.service.ArtworkService;
import com.example.artsphere.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtworkRepository artworkRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SellerUserFollowRepository followRepository;

    @Mock
    private ArtworkService artworkService;

    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AdminController adminController;

    private User testUser;
    private User testArtist;
    private Category testCategory;
    private Order testOrder;
    private Artwork testArtwork;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("buyer_jan");
        testUser.setEmail("buyer@example.com");
        testUser.setRole("BUYER");
        testUser.setBalance(BigDecimal.valueOf(500.00));
        testUser.setIsActive(true);
        testUser.setIsVerified(true);
        testUser.setCreatedAt(LocalDateTime.now());

        testArtist = new User();
        testArtist.setId(2L);
        testArtist.setUsername("artist_anna");
        testArtist.setEmail("artist@example.com");
        testArtist.setRole("ARTIST");
        testArtist.setFirstName("Anna");
        testArtist.setLastName("Kowalska");
        testArtist.setBalance(BigDecimal.valueOf(1000.00));
        testArtist.setIsActive(true);
        testArtist.setIsVerified(true);
        testArtist.setCreatedAt(LocalDateTime.now());

        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Malarstwo");
        testCategory.setDescription("Obrazy");
        testCategory.setSlug("malarstwo");
        testCategory.setIsActive(true);
        testCategory.setCreatedAt(LocalDateTime.now());

        testArtwork = new Artwork();
        testArtwork.setId(1L);
        testArtwork.setTitle("Obraz testowy");
        testArtwork.setUser(testArtist);
        testArtwork.setStatus("AVAILABLE");
        testArtwork.setIsSold(false);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus("PENDING");
        testOrder.setPaymentStatus("PAID");
        testOrder.setTotalPrice(BigDecimal.valueOf(500.00));
        testOrder.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testArtist));

        // Act
        ResponseEntity<List<AdminUserResponse>> response = adminController.getAllUsers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        List<AdminUserResponse> users = response.getBody();
        assertEquals(2, users.size());
        assertEquals("buyer_jan", users.get(0).getUsername());
        assertEquals("artist_anna", users.get(1).getUsername());
    }

    @Test
    @DisplayName("Should return only artists in sellers endpoint")
    void shouldReturnOnlyArtists() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testArtist));
        when(followRepository.findBySellerId(2L)).thenReturn(Collections.emptyList());
        when(artworkRepository.findByUserId(2L)).thenReturn(Collections.emptyList());
        when(saleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AdminSellerResponse>> response = adminController.getAllSellers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        List<AdminSellerResponse> sellers = response.getBody();
        assertEquals(1, sellers.size());
        assertEquals("artist_anna", sellers.get(0).getUsername());
        assertEquals("ARTIST", sellers.get(0).getRole());
    }

    @Test
    @DisplayName("Should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        // Arrange
        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRole("ARTIST");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = adminController.updateUserRole(1L, request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("ARTIST", testUser.getRole());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should update user status successfully")
    void shouldUpdateUserStatusSuccessfully() {
        // Arrange
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = adminController.updateUserStatus(1L, request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(false, testUser.getIsActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should update artwork status successfully")
    void shouldUpdateArtworkStatusSuccessfully() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("status", "SOLD");

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));

        // Act
        ResponseEntity<?> response = adminController.updateArtworkStatus(1L, request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("SOLD", testArtwork.getStatus());
        assertTrue(testArtwork.getIsSold());
        verify(artworkRepository, times(1)).save(testArtwork);
    }

    @Test
    @DisplayName("Should get dashboard statistics successfully")
    void shouldGetDashboardStatisticsSuccessfully() {
        // Arrange
        AdminDashboardStatsDto mockStats = new AdminDashboardStatsDto();
        mockStats.setTotalUsers(100);
        mockStats.setTotalOrders(50);
        mockStats.setTotalTransactionValue(10000.0);

        when(userService.getAdminDashboardStatistics()).thenReturn(mockStats);

        // Act
        ResponseEntity<AdminDashboardStatsDto> response = adminController.getDashboardStatistics();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        AdminDashboardStatsDto stats = response.getBody();
        assertEquals(100, stats.getTotalUsers());
        assertEquals(50, stats.getTotalOrders());
        assertEquals(10000.0, stats.getTotalTransactionValue());
    }
}