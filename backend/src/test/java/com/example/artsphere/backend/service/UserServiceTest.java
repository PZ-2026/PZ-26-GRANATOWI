package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.*;
import com.example.artsphere.backend.model.*;
import com.example.artsphere.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SellerUserFollowRepository followRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ArtworkRepository artworkRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("art_lover");
        testUser.setEmail("art@example.com");
        testUser.setFirstName("Jan");
        testUser.setLastName("Kowalski");
        testUser.setRole("BUYER");
        testUser.setBalance(BigDecimal.valueOf(100.0));
        testUser.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 0));
    }

    // getUserProfile

    @Test
    @DisplayName("Should return user profile when user exists")
    void shouldReturnUserProfileWhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        LoginResponse response = userService.getUserProfile(1L);

        // Assert
        assertEquals("art_lover", response.getUsername());
        assertEquals("art@example.com", response.getEmail());
        assertEquals("Jan", response.getFirstName());
        assertEquals("Kowalski", response.getLastName());
        assertEquals("BUYER", response.getRole());
        assertEquals(100.0, response.getBalance().doubleValue());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return zero balance when user balance is null")
    void shouldReturnZeroBalanceWhenUserBalanceIsNull() {
        // Arrange
        testUser.setBalance(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        LoginResponse response = userService.getUserProfile(1L);

        // Assert
        assertEquals(BigDecimal.ZERO, response.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when user profile not found")
    void shouldThrowExceptionWhenUserProfileNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserProfile(99L));
    }

    // updateUserProfile

    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("new_username");
        request.setEmail("newemail@example.com");
        request.setFirstName("Adam");
        request.setLastName("Nowak");
        request.setPassword("newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("new_username")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());

        // Act
        String result = userService.updateUserProfile(1L, request);

        // Assert
        assertEquals("Twój profil został zaktualizowany!", result);
        assertEquals("new_username", testUser.getUsername());
        assertEquals("newemail@example.com", testUser.getEmail());
        assertEquals("Adam", testUser.getFirstName());
        assertEquals("Nowak", testUser.getLastName());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when username is already taken")
    void shouldThrowExceptionWhenUsernameIsTaken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing_user");
        request.setEmail("art@example.com");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setUsername("existing_user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("existing_user")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserProfile(1L, request));
        assertEquals("Nazwa użytkownika jest już zajęta.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email is already taken")
    void shouldThrowExceptionWhenEmailIsTaken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("art_lover");
        request.setEmail("taken@example.com");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("taken@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserProfile(1L, request));
        assertEquals("Adres e-mail jest już zajęty.", exception.getMessage());
    }

    @Test
    @DisplayName("Should not update password when password is empty")
    void shouldNotUpdatePasswordWhenPasswordIsEmpty() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("art_lover");
        request.setEmail("art@example.com");
        request.setPassword("   ");

        String originalPassword = testUser.getPassword();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        userService.updateUserProfile(1L, request);

        // Assert
        assertEquals(originalPassword, testUser.getPassword());
    }

    // addBalance

    @Test
    @DisplayName("Should increase balance and save transaction")
    void shouldIncreaseBalanceWhenAddingFunds() {
        // Arrange
        testUser.setBalance(BigDecimal.valueOf(50.0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Double result = userService.addBalance(1L, 150.0);

        // Assert
        assertEquals(200.0, result);
        verify(userRepository).save(testUser);
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    @DisplayName("Should handle null balance when adding funds")
    void shouldHandleNullBalanceWhenAddingFunds() {
        // Arrange
        testUser.setBalance(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Double result = userService.addBalance(1L, 100.0);

        // Assert
        assertEquals(100.0, result);
    }

    // deductBalance

    @Test
    @DisplayName("Should decrease balance when funds are sufficient")
    void shouldDecreaseBalanceWhenFundsAreSufficient() {
        // Arrange
        testUser.setBalance(BigDecimal.valueOf(100.0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Double result = userService.deductBalance(1L, 40.0);

        // Assert
        assertEquals(60.0, result);
        verify(userRepository).save(any(User.class));
        verify(walletTransactionRepository).save(any(WalletTransaction.class));
    }

    @Test
    @DisplayName("Should throw exception when deducting more than user has")
    void shouldThrowExceptionWhenInsufficientFunds() {
        // Arrange
        testUser.setBalance(BigDecimal.valueOf(10.0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deductBalance(1L, 100.0));

        assertEquals("Brak wystarczających środków w portfelu.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // getUserTransactions

    @Test
    @DisplayName("Should return list of user transactions")
    void shouldReturnListOfUserTransactions() {
        // Arrange
        WalletTransaction tx1 = new WalletTransaction();
        tx1.setTitle("Wpłata na portfel");
        tx1.setAmount(BigDecimal.valueOf(100.0));
        tx1.setTransactionDate(LocalDateTime.of(2024, 4, 1, 12, 0));
        tx1.setIncome(true);

        WalletTransaction tx2 = new WalletTransaction();
        tx2.setTitle("Wypłata z portfela / Płatność");
        tx2.setAmount(BigDecimal.valueOf(50.0));
        tx2.setTransactionDate(LocalDateTime.of(2024, 4, 2, 14, 30));
        tx2.setIncome(false);

        when(walletTransactionRepository.findByUserIdOrderByTransactionDateDesc(1L))
                .thenReturn(Arrays.asList(tx1, tx2));

        // Act
        List<TransactionDto> transactions = userService.getUserTransactions(1L);

        // Assert
        assertEquals(2, transactions.size());
        assertEquals("Wpłata na portfel", transactions.get(0).getTitle());
        assertEquals(100.0, transactions.get(0).getAmount());
        assertTrue(transactions.get(0).isIncome());
        assertEquals("01.04.2024 12:00", transactions.get(0).getDate());
    }

    @Test
    @DisplayName("Should return empty list when user has no transactions")
    void shouldReturnEmptyListWhenNoTransactions() {
        // Arrange
        when(walletTransactionRepository.findByUserIdOrderByTransactionDateDesc(1L))
                .thenReturn(Collections.emptyList());

        // Act
        List<TransactionDto> transactions = userService.getUserTransactions(1L);

        // Assert
        assertTrue(transactions.isEmpty());
    }

    // getClientStatistics

    @Test
    @DisplayName("Should calculate client statistics correctly")
    void shouldCalculateClientStatisticsCorrectly() {
        // Arrange
        Order order1 = new Order();
        order1.setTotalPrice(BigDecimal.valueOf(150.0));
        order1.setCreatedAt(LocalDateTime.now());

        Order order2 = new Order();
        order2.setTotalPrice(BigDecimal.valueOf(200.0));
        order2.setCreatedAt(LocalDateTime.now().minusMonths(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order1, order2));
        when(followRepository.findByUserId(1L)).thenReturn(Arrays.asList(new SellerUserFollow(), new SellerUserFollow()));

        // Act
        ClientStatisticsDto stats = userService.getClientStatistics(1L);

        // Assert
        assertEquals(350.0, stats.getTotalSpent());
        assertEquals(2, stats.getTotalPurchases());
        assertEquals(2, stats.getFavoriteArtistsCount());
        assertEquals(150.0, stats.getSpentThisMonth());
        assertEquals(1, stats.getPurchasesThisMonth());
        assertEquals("Styczeń 2024", stats.getMemberSince());
    }

    @Test
    @DisplayName("Should handle user with no orders")
    void shouldHandleUserWithNoOrders() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(followRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act
        ClientStatisticsDto stats = userService.getClientStatistics(1L);

        // Assert
        assertEquals(0.0, stats.getTotalSpent());
        assertEquals(0, stats.getTotalPurchases());
        assertEquals(0, stats.getPurchasesThisMonth());
    }

    // getSellerStatistics

    @Test
    @DisplayName("Should calculate seller statistics correctly")
    void shouldCalculateSellerStatisticsCorrectly() {
        // Arrange
        Long sellerId = 1L;

        Artwork artwork1 = new Artwork();
        artwork1.setTitle("Piękny obraz");
        artwork1.setStatus("AVAILABLE");
        artwork1.setUser(testUser);

        Artwork artwork2 = new Artwork();
        artwork2.setTitle("Sprzedany obraz");
        artwork2.setStatus("SOLD");
        artwork2.setUser(testUser);

        Sale sale1 = new Sale();
        sale1.setPrice(BigDecimal.valueOf(500.0));
        sale1.setSoldAt(LocalDateTime.now());
        sale1.setArtwork(artwork2);

        Sale sale2 = new Sale();
        sale2.setPrice(BigDecimal.valueOf(300.0));
        sale2.setSoldAt(LocalDateTime.now().minusMonths(2));
        sale2.setArtwork(artwork1);

        when(artworkRepository.findByUserId(sellerId)).thenReturn(Arrays.asList(artwork1, artwork2));
        when(saleRepository.findAll()).thenReturn(Arrays.asList(sale1, sale2));
        when(followRepository.findBySellerId(sellerId)).thenReturn(Arrays.asList(new SellerUserFollow()));

        // Act
        var stats = userService.getSellerStatistics(sellerId);

        // Assert
        assertEquals(2, stats.getTotalSales());
        assertEquals(800.0, stats.getTotalRevenue());
        assertEquals(1, stats.getFollowerCount());
        assertEquals(2, stats.getTotalArtworks());
        assertEquals(1, stats.getActiveListings());
        assertEquals(1, stats.getSoldThisMonth());
        assertEquals(500.0, stats.getRevenueThisMonth());
        assertEquals("Sprzedany obraz", stats.getTopArtworkTitle());
    }

    @Test
    @DisplayName("Should handle seller with no sales")
    void shouldHandleSellerWithNoSales() {
        // Arrange
        when(artworkRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(saleRepository.findAll()).thenReturn(Collections.emptyList());
        when(followRepository.findBySellerId(1L)).thenReturn(Collections.emptyList());

        // Act
        var stats = userService.getSellerStatistics(1L);

        // Assert
        assertEquals(0, stats.getTotalSales());
        assertEquals(0.0, stats.getTotalRevenue());
        assertEquals("Brak", stats.getTopArtworkTitle());
        assertEquals(0, stats.getTopArtworkSales());
    }

    // getAdminDashboardStatistics

    @Test
    @DisplayName("Should calculate admin dashboard statistics correctly")
    void shouldCalculateAdminDashboardStatisticsCorrectly() {
        // Arrange
        User buyer = new User();
        buyer.setRole("BUYER");
        buyer.setCreatedAt(LocalDateTime.now());
        buyer.setBalance(BigDecimal.valueOf(100.0));

        User artist = new User();
        artist.setRole("ARTIST");
        artist.setCreatedAt(LocalDateTime.now().minusMonths(2));
        artist.setBalance(BigDecimal.valueOf(500.0));

        Artwork artwork1 = new Artwork();
        artwork1.setStatus("AVAILABLE");
        artwork1.setIsSold(false);

        Artwork artwork2 = new Artwork();
        artwork2.setStatus("SOLD");
        artwork2.setIsSold(true);

        Order order1 = new Order();
        order1.setStatus("COMPLETED");
        order1.setTotalPrice(BigDecimal.valueOf(200.0));

        Order order2 = new Order();
        order2.setStatus("PENDING");
        order2.setTotalPrice(BigDecimal.valueOf(150.0));

        Sale sale = new Sale();
        sale.setPrice(BigDecimal.valueOf(300.0));

        when(userRepository.findAll()).thenReturn(Arrays.asList(buyer, artist));
        when(artworkRepository.findAll()).thenReturn(Arrays.asList(artwork1, artwork2));
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        when(saleRepository.findAll()).thenReturn(Collections.singletonList(sale));

        // Act
        AdminDashboardStatsDto stats = userService.getAdminDashboardStatistics();

        // Assert
        assertEquals(2, stats.getTotalUsers());
        assertEquals(1, stats.getTotalSellers());
        assertEquals(1, stats.getTotalBuyers());
        assertEquals(1, stats.getNewUsersThisMonth());
        assertEquals(2, stats.getTotalArtworks());
        assertEquals(1, stats.getActiveListings());
        assertEquals(1, stats.getSoldArtworks());
        assertEquals(2, stats.getTotalOrders());
        assertEquals(1, stats.getPendingOrders());
        assertEquals(1, stats.getCompletedOrders());
        assertEquals(350.0, stats.getTotalTransactionValue());
        assertEquals(175.0, stats.getAverageOrderValue());
        assertEquals(300.0, stats.getPlatformRevenue());
        assertEquals(300.0, stats.getAverageUserBalance());
    }

    @Test
    @DisplayName("Should handle empty database for admin statistics")
    void shouldHandleEmptyDatabaseForAdminStatistics() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(artworkRepository.findAll()).thenReturn(Collections.emptyList());
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(saleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        AdminDashboardStatsDto stats = userService.getAdminDashboardStatistics();

        // Assert
        assertEquals(0, stats.getTotalUsers());
        assertEquals(0, stats.getTotalOrders());
        assertEquals(0.0, stats.getTotalTransactionValue());
        assertEquals(0.0, stats.getAverageOrderValue());
        assertEquals(0.0, stats.getAverageUserBalance());
    }
}