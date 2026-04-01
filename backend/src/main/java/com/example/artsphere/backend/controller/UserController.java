package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.LoginResponse;
import com.example.artsphere.backend.dto.RegisterRequest;
import com.example.artsphere.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            LoginResponse response = userService.getUserProfile(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody RegisterRequest updateRequest) {
        try {
            String message = userService.updateUserProfile(userId, updateRequest);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}