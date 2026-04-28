package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.dto.TransactionDto;
import com.example.artsphere.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUserProfileShouldReturnUserData() {
        LoginResponse profile = new LoginResponse(1L, "jan", "jan@example.com", "Jan", "Kowalski", "BUYER", "OK", BigDecimal.valueOf(200));
        when(userService.getUserProfile(1L)).thenReturn(profile);

        ResponseEntity<?> result = userController.getUserProfile(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(profile, result.getBody());
    }

    @Test
    void getUserProfileShouldReturnBadRequestOnError() {
        when(userService.getUserProfile(1L)).thenThrow(new RuntimeException("Brak usera"));

        ResponseEntity<?> result = userController.getUserProfile(1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(Map.class, result.getBody());
        assertEquals("Brak usera", ((Map<?, ?>) result.getBody()).get("error"));
    }

    @Test
    void addBalanceShouldReturnNewBalance() {
        when(userService.addBalance(1L, 50.0)).thenReturn(150.0);

        ResponseEntity<?> result = userController.addBalance(1L, 50.0);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(150.0, ((Map<?, ?>) result.getBody()).get("newBalance"));
        verify(userService).addBalance(1L, 50.0);
    }

    @Test
    void getTransactionsShouldReturnList() {
        TransactionDto tx = new TransactionDto("Wpłata", 100.0, "01.01.2026 10:00", true);
        when(userService.getUserTransactions(1L)).thenReturn(List.of(tx));

        ResponseEntity<List<TransactionDto>> result = userController.getTransactions(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("Wpłata", result.getBody().getFirst().getTitle());
    }

    @Test
    void updateUserProfileShouldReturnMessageMap() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nowy");
        when(userService.updateUserProfile(1L, request)).thenReturn("Twój profil został zaktualizowany!");

        ResponseEntity<?> result = userController.updateUserProfile(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Twój profil został zaktualizowany!", ((Map<?, ?>) result.getBody()).get("message"));
    }
}
