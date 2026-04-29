package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.CreateOrderRequest;
import com.example.artsphere.backend.dto.PurchaseResponse;
import com.example.artsphere.backend.model.*;
import com.example.artsphere.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtworkRepository artworkRepository;

    @Mock
    private WalletTransactionRepository walletTransactionRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @InjectMocks
    private OrderController orderController;

    private User testBuyer;
    private User testArtist;
    private Artwork testArtwork;
    private Address testAddress;
    private Order testOrder;
    private OrderItem testOrderItem;
    private Sale testSale;

    @BeforeEach
    void setUp() {
        testBuyer = new User();
        testBuyer.setId(1L);
        testBuyer.setUsername("buyer_jan");
        testBuyer.setEmail("buyer@example.com");
        testBuyer.setFirstName("Jan");
        testBuyer.setLastName("Kowalski");
        testBuyer.setBalance(BigDecimal.valueOf(5000.00));
        testBuyer.setCreatedAt(LocalDateTime.now());

        testArtist = new User();
        testArtist.setId(2L);
        testArtist.setUsername("artist_anna");
        testArtist.setBalance(BigDecimal.valueOf(1000.00));

        testArtwork = new Artwork();
        testArtwork.setId(1L);
        testArtwork.setTitle("Piękny obraz");
        testArtwork.setArtist("Anna Nowak");
        testArtwork.setPrice(BigDecimal.valueOf(500.00));
        testArtwork.setUser(testArtist);
        testArtwork.setStatus("AVAILABLE");
        testArtwork.setIsSold(false);

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUser(testBuyer);
        testAddress.setCity("Warszawa");
        testAddress.setStreet("Marszałkowska");
        testAddress.setPostalCode("00-001");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testBuyer);
        testOrder.setTotalPrice(BigDecimal.valueOf(500.00));
        testOrder.setStatus("PENDING");
        testOrder.setPaymentStatus("PAID");
        testOrder.setCreatedAt(LocalDateTime.now());

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setArtwork(testArtwork);
        testOrderItem.setPrice(BigDecimal.valueOf(500.00));
        testOrderItem.setQuantity(1);

        testSale = new Sale();
        testSale.setId(1L);
        testSale.setArtwork(testArtwork);
        testSale.setBuyer(testBuyer);
        testSale.setPrice(BigDecimal.valueOf(500.00));
        testSale.setSoldAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create order successfully during checkout")
    void shouldCreateOrderSuccessfullyDuringCheckout() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setArtworkIds(Collections.singletonList(1L));
        request.setTotalPrice(500.0);
        request.setPaymentMethod("Portfel ArtSphere");
        request.setAddressId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testBuyer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));

        // Act
        ResponseEntity<?> response = orderController.checkout(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(saleRepository, times(1)).save(any(Sale.class));
        verify(artworkRepository, times(1)).save(any(Artwork.class));
    }

    @Test
    @DisplayName("Should update artist balance after purchase")
    void shouldUpdateArtistBalanceAfterPurchase() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setArtworkIds(Collections.singletonList(1L));
        request.setTotalPrice(500.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testBuyer));
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));

        // Act
        orderController.checkout(request);

        // Assert
        assertEquals(1500.0, testArtist.getBalance().doubleValue()); // 1000 + 500
        verify(userRepository, times(1)).save(testArtist);
        verify(walletTransactionRepository, times(1)).save(any(WalletTransaction.class));
    }

    @Test
    @DisplayName("Should mark artwork as sold after checkout")
    void shouldMarkArtworkAsSoldAfterCheckout() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setArtworkIds(Collections.singletonList(1L));
        request.setTotalPrice(500.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testBuyer));
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(testArtwork));

        // Act
        orderController.checkout(request);

        // Assert
        assertTrue(testArtwork.getIsSold());
        assertEquals("SOLD", testArtwork.getStatus());
        verify(artworkRepository, times(1)).save(testArtwork);
    }

    @Test
    @DisplayName("Should return user purchases successfully")
    void shouldReturnUserPurchasesSuccessfully() {
        // Arrange
        when(orderRepository.findByUserId(1L)).thenReturn(Collections.singletonList(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Collections.singletonList(testOrderItem));

        // Act
        ResponseEntity<List<PurchaseResponse>> response = orderController.getUserPurchases(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        List<PurchaseResponse> purchases = response.getBody();
        assertEquals(1, purchases.size());
        assertEquals("Piękny obraz", purchases.get(0).getTitle());
        assertEquals(500.0, purchases.get(0).getPrice());
    }

    @Test
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrderSuccessfully() {
        // Arrange
        doNothing().when(orderRepository).deleteById(1L);

        // Act
        ResponseEntity<?> response = orderController.deleteOrder(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Usunięto pomyślnie z historii", body.get("message"));
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return top fans for seller")
    void shouldReturnTopFansForSeller() {
        // Arrange
        when(saleRepository.findAll()).thenReturn(Collections.singletonList(testSale));

        // Act
        ResponseEntity<?> response = orderController.getTopFans(2L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        List<Map<String, Object>> fans = (List<Map<String, Object>>) response.getBody();
        assertEquals(1, fans.size());
        assertEquals("Jan Kowalski", fans.get(0).get("name"));
        assertEquals(1, fans.get(0).get("purchaseCount"));
    }
}