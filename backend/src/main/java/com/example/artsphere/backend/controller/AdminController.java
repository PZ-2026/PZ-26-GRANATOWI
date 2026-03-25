package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/hash-passwords")
    public Map<String, String> hashAllPasswords() {
        List<User> users = userRepository.findAll();
        int updated = 0;
        
        for (User user : users) {
            // Zahashuj tylko jeśli hasło nie wygląda na hash BCrypt
            if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                String plainPassword = user.getPassword();
                user.setPassword(passwordEncoder.encode(plainPassword));
                userRepository.save(user);
                updated++;
            }
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Zaktualizowano " + updated + " użytkowników");
        result.put("total", String.valueOf(users.size()));
        return result;
    }
    
    @GetMapping("/check-passwords")
    public Map<String, Object> checkPasswords() {
        List<User> users = userRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        
        for (User user : users) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", user.getEmail());
            userInfo.put("passwordIsHashed", user.getPassword().startsWith("$2a$") ? "YES" : "NO");
            userInfo.put("passwordPreview", user.getPassword().substring(0, Math.min(30, user.getPassword().length())));
            result.put(user.getUsername(), userInfo);
        }
        
        return result;
    }
    
    @GetMapping("/get-full-hashes")
    public Map<String, String> getFullHashes() {
        List<User> users = userRepository.findAll();
        Map<String, String> result = new HashMap<>();
        
        for (User user : users) {
            result.put(user.getEmail(), user.getPassword());
        }
        
        return result;
    }
}
