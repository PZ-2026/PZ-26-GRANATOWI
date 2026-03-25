package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginRequest;
import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.model.Role;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.RoleRepository;
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
    private RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Nieprawidłowy e-mail lub hasło");
        }

        User user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Nieprawidłowy e-mail lub hasło");
        }

        String roleName = user.getRole() != null ? user.getRole().getName() : "BUYER";
        
        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            roleName,
            "Zalogowano pomyślnie"
        );
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Użytkownik o takiej nazwie już istnieje");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
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
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.findByName("BUYER").orElse(null));
        user.setRole(role);

        userRepository.save(user);
        return "Zarejestrowano pomyślnie";
    }
}
