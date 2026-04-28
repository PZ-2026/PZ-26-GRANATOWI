package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.LoginRequest;
import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginShouldReturnOkWhenCredentialsAreCorrect() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("secret");

        LoginResponse response = new LoginResponse(1L, "test", "test@example.com", "Jan", "Kowalski", "BUYER", "OK", BigDecimal.TEN);
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<?> result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(authService).login(request);
    }

    @Test
    void loginShouldReturnBadRequestWhenServiceThrows() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong");
        when(authService.login(request)).thenThrow(new RuntimeException("Nieprawidłowe dane"));

        ResponseEntity<?> result = authController.login(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(String.class, result.getBody());
        assertEquals("Nieprawidłowe dane", result.getBody());
    }

    @Test
    void registerShouldReturnOkWithMessage() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("pass");
        when(authService.register(request)).thenReturn("Zarejestrowano pomyślnie");

        ResponseEntity<?> result = authController.register(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Zarejestrowano pomyślnie", result.getBody());
        verify(authService).register(request);
    }
}
