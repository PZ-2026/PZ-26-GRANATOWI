package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginRequest;
import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.logging.AuthFileLogger;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthFileLogger authFileLogger;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            authFileLogger.logLogin(request.getEmail(), false, "Nieprawidłowy e-mail lub hasło");
            throw new RuntimeException("Nieprawidłowy e-mail lub hasło");
        }

        User user = userOpt.get();

        if (Boolean.FALSE.equals(user.getIsActive())) {
            authFileLogger.logLogin(user.getEmail(), false, "Konto jest nieaktywne");
            throw new RuntimeException("Konto jest nieaktywne");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            authFileLogger.logLogin(request.getEmail(), false, "Nieprawidłowy e-mail lub hasło");
            throw new RuntimeException("Nieprawidłowy e-mail lub hasło");
        }

        String roleName = user.getRole() != null ? user.getRole() : "BUYER";
        BigDecimal userBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;

        authFileLogger.logLogin(user.getEmail(), true, "Zalogowano pomyślnie");

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roleName,
                "Zalogowano pomyślnie",
                userBalance
        );
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            authFileLogger.logRegister(request.getUsername(), request.getEmail(), false, "Użytkownik o takiej nazwie już istnieje");
            throw new RuntimeException("Użytkownik o takiej nazwie już istnieje");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            authFileLogger.logRegister(request.getUsername(), request.getEmail(), false, "Użytkownik o takim e-mail już istnieje");
            throw new RuntimeException("Użytkownik o takim e-mail już istnieje");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBalance(BigDecimal.ZERO);

        String roleName = request.getRoleName() != null ? request.getRoleName() : "BUYER";
        user.setRole(roleName);

        userRepository.save(user);
        authFileLogger.logRegister(request.getUsername(), request.getEmail(), true, "Zarejestrowano pomyślnie");
        return "Zarejestrowano pomyślnie";
    }
}
