package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.dto.TransactionDto;
import com.example.artsphere.backend.dto.ClientStatisticsDto;
import com.example.artsphere.backend.model.Order;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.model.WalletTransaction;
import com.example.artsphere.backend.repository.OrderRepository;
import com.example.artsphere.backend.repository.SellerUserFollowRepository;
import com.example.artsphere.backend.repository.UserRepository;
import com.example.artsphere.backend.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SellerUserFollowRepository followRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        BigDecimal userBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole() : "BUYER",
                "Pobrano profil",
                userBalance
        );
    }

    public String updateUserProfile(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Brak użytkownika"));

        if (!user.getUsername().equals(request.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) throw new RuntimeException("Nazwa użytkownika jest już zajęta.");
        }

        if (!user.getEmail().equals(request.getEmail())) {
            Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());
            if (existingEmail.isPresent()) throw new RuntimeException("Adres e-mail jest już zajęty.");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        return "Twój profil został zaktualizowany!";
    }

    public Double addBalance(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Brak użytkownika"));
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        user.setBalance(currentBalance.add(BigDecimal.valueOf(amount)));
        userRepository.save(user);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTitle("Wpłata na portfel");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setIncome(true);
        walletTransactionRepository.save(transaction);

        return user.getBalance().doubleValue();
    }

    public Double deductBalance(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Brak użytkownika"));
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal toDeduct = BigDecimal.valueOf(amount);

        if (currentBalance.compareTo(toDeduct) < 0) {
            throw new RuntimeException("Brak wystarczających środków w portfelu.");
        }

        user.setBalance(currentBalance.subtract(toDeduct));
        userRepository.save(user);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTitle("Wypłata z portfela / Płatność");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setIncome(false);
        walletTransactionRepository.save(transaction);

        return user.getBalance().doubleValue();
    }

    public List<TransactionDto> getUserTransactions(Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        List<WalletTransaction> transactions = walletTransactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        return transactions.stream().map(tx -> new TransactionDto(
                tx.getTitle(),
                tx.getAmount().doubleValue(),
                tx.getTransactionDate().format(formatter),
                tx.isIncome()
        )).collect(Collectors.toList());
    }

    public ClientStatisticsDto getClientStatistics(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Order> orders = orderRepository.findByUserId(userId);

        double totalSpent = 0.0;
        double spentThisMonth = 0.0;
        int purchasesThisMonth = 0;

        LocalDateTime now = LocalDateTime.now();
        for (Order o : orders) {
            double price = o.getTotalPrice() != null ? o.getTotalPrice().doubleValue() : 0.0;
            totalSpent += price;

            if (o.getCreatedAt() != null && o.getCreatedAt().getMonth() == now.getMonth() && o.getCreatedAt().getYear() == now.getYear()) {
                spentThisMonth += price;
                purchasesThisMonth++;
            }
        }

        int favArtists = followRepository.findByUserId(userId).size();

        ClientStatisticsDto dto = new ClientStatisticsDto();
        dto.setTotalSpent(totalSpent);
        dto.setTotalPurchases(orders.size());
        dto.setFavoriteArtistsCount(favArtists);
        dto.setSpentThisMonth(spentThisMonth);
        dto.setPurchasesThisMonth(purchasesThisMonth);

        String memberSince = "Od zawsze";
        if (user.getCreatedAt() != null) {
            String month = user.getCreatedAt().getMonth().getDisplayName(
                    java.time.format.TextStyle.FULL_STANDALONE,
                    new java.util.Locale("pl", "PL")
            );
            month = month.substring(0, 1).toUpperCase() + month.substring(1);
            int year = user.getCreatedAt().getYear();
            memberSince = month + " " + year;
        }
        dto.setMemberSince(memberSince);

        dto.setWishlistCount(0);
        dto.setReviewsGiven(0);
        dto.setAverageRating(0.0f);
        dto.setSavedArtworks(0);

        return dto;
    }

    @Autowired
    private com.example.artsphere.backend.repository.SaleRepository saleRepository;

    @Autowired
    private com.example.artsphere.backend.repository.ArtworkRepository artworkRepository;

    public com.example.artsphere.backend.dto.SellerStatisticsDto getSellerStatistics(Long sellerId) {
        // Wszystkie dzieła wystawione przez sprzedawcę
        List<com.example.artsphere.backend.model.Artwork> sellerArtworks = artworkRepository.findByUserId(sellerId);

        // Wszystkie jego sprzedaże
        List<com.example.artsphere.backend.model.Sale> allSales = saleRepository.findAll().stream()
                .filter(s -> s.getArtwork() != null && s.getArtwork().getUser() != null && s.getArtwork().getUser().getId().equals(sellerId))
                .collect(Collectors.toList());

        int totalSales = allSales.size();
        double totalRevenue = allSales.stream().mapToDouble(s -> s.getPrice() != null ? s.getPrice().doubleValue() : 0.0).sum();

        int soldThisMonth = 0;
        double revenueThisMonth = 0.0;
        LocalDateTime now = LocalDateTime.now();

        // Ze względu na specyfikę projektu, poszukajmy dzieła, które przyniosło najwięcej pieniędzy (lub zostało sprzedane)
        String topArtworkTitle = "Brak";
        double maxArtworkPrice = 0.0;

        for (com.example.artsphere.backend.model.Sale s : allSales) {
            if (s.getSoldAt() != null && s.getSoldAt().getMonth() == now.getMonth() && s.getSoldAt().getYear() == now.getYear()) {
                soldThisMonth++;
                revenueThisMonth += s.getPrice() != null ? s.getPrice().doubleValue() : 0.0;
            }

            if (s.getPrice() != null && s.getPrice().doubleValue() > maxArtworkPrice) {
                maxArtworkPrice = s.getPrice().doubleValue();
                topArtworkTitle = s.getArtwork() != null ? s.getArtwork().getTitle() : "Brak tytułu";
            }
        }

        int activeListings = (int) sellerArtworks.stream().filter(a -> "AVAILABLE".equals(a.getStatus())).count();
        int followersCount = followRepository.findBySellerId(sellerId).size();

        com.example.artsphere.backend.dto.SellerStatisticsDto dto = new com.example.artsphere.backend.dto.SellerStatisticsDto();
        dto.setTotalSales(totalSales);
        dto.setTotalRevenue(totalRevenue);
        dto.setAverageRating(5.0f); // W przyszłości z systemu opinii
        dto.setFollowerCount(followersCount);
        dto.setTotalArtworks(sellerArtworks.size());
        dto.setActiveListings(activeListings);
        dto.setSoldThisMonth(soldThisMonth);
        dto.setRevenueThisMonth(revenueThisMonth);
        dto.setTopArtworkTitle(topArtworkTitle);
        dto.setTopArtworkSales(topArtworkTitle.equals("Brak") ? 0 : 1);
        dto.setPendingOrders(0); // Założenie domyślne dla aplikacji (opłacone to completed)
        dto.setCompletedOrders(totalSales);

        return dto;
    }
}