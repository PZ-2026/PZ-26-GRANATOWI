package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() != null ? user.getRole() : "BUYER",
                "Pobrano profil"
        );
    }

    public String updateUserProfile(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        // Walidacja nazwy użytkownika
        if (!user.getUsername().equals(request.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                throw new RuntimeException("Nazwa użytkownika jest już zajęta.");
            }
        }

        // Walidacja adresu email
        if (!user.getEmail().equals(request.getEmail())) {
            Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());
            if (existingEmail.isPresent()) {
                throw new RuntimeException("Adres e-mail jest już zajęty.");
            }
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Zapisujemy nowe hasło tylko jeśli zostało wpisane
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return "Twój profil został zaktualizowany!";
    }
}