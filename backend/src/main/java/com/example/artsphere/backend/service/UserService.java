package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.dto.TransactionDto;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.model.WalletTransaction;
import com.example.artsphere.backend.repository.UserRepository;
import com.example.artsphere.backend.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    // --- LOGIKA PORTFELA z zapisem historii ---

    public Double addBalance(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Brak użytkownika"));
        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        // Zmiana i zapis salda
        user.setBalance(currentBalance.add(BigDecimal.valueOf(amount)));
        userRepository.save(user);

        // Tworzenie rekordu historii operacji
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

        // Zmiana i zapis salda
        user.setBalance(currentBalance.subtract(toDeduct));
        userRepository.save(user);

        // Tworzenie rekordu historii operacji
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTitle("Wypłata z portfela / Płatność");
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setIncome(false);
        walletTransactionRepository.save(transaction);

        return user.getBalance().doubleValue();
    }

    // --- ZWRACANIE HISTORII ---

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
}