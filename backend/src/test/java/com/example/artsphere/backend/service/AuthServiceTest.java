package com.example.artsphere.backend.service;

import com.example.artsphere.backend.dto.LoginRequest;
import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.logging.AuthFileLogger;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import com.example.artsphere.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthFileLogger authFileLogger;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder realPasswordEncoder;
    private User testUser;

    @BeforeEach
    void setUp() {
        realPasswordEncoder = new BCryptPasswordEncoder();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword(realPasswordEncoder.encode("secret123")); // Zahashowane hasło
        testUser.setIsActive(true);
        testUser.setRole("BUYER");
        testUser.setBalance(BigDecimal.valueOf(100.0));
    }

    // Testy LOGIN

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void shouldLoginSuccessfully() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("Zalogowano pomyślnie", response.getMessage());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception for non-existent email")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Nieprawidłowy e-mail lub hasło", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when password is wrong")
    void shouldThrowExceptionWhenPasswordIsWrong() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong_password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Nieprawidłowy e-mail lub hasło", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when account is inactive")
    void shouldThrowExceptionWhenAccountIsInactive() {
        // Arrange
        testUser.setIsActive(false);
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Konto jest nieaktywne", ex.getMessage());
    }

    // Testy REGISTER

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterSuccessfully() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRoleName("ARTIST");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        // Act
        String result = authService.register(request);

        // Assert
        assertEquals("Zarejestrowano pomyślnie", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username is already taken")
    void shouldThrowExceptionWhenUsernameTaken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("taken_name");

        when(userRepository.findByUsername("taken_name")).thenReturn(Optional.of(new User()));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Użytkownik o takiej nazwie już istnieje", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email is already taken")
    void shouldThrowExceptionWhenEmailTaken() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("fresh_name");
        request.setEmail("taken@example.com");

        when(userRepository.findByUsername("fresh_name")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Użytkownik o takim e-mail już istnieje", ex.getMessage());
        verify(userRepository, never()).save(any());
    }
}