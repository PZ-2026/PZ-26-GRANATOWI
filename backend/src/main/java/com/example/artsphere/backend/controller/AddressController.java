package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AddressRequest;
import com.example.artsphere.backend.dto.AddressResponse;
import com.example.artsphere.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAddress(@PathVariable Long userId, @RequestBody AddressRequest request) {
        try { return ResponseEntity.ok(addressService.createAddress(userId, request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @PutMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId, @PathVariable Long userId, @RequestBody AddressRequest request) {
        try { return ResponseEntity.ok(addressService.updateAddress(addressId, userId, request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @DeleteMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId, @PathVariable Long userId) {
        try {
            addressService.deleteAddress(addressId, userId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usunięto"));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() { return ResponseEntity.ok(addressService.getAllAddresses()); }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) { return ResponseEntity.ok(addressService.getAddressById(id)); }

    @PutMapping("/admin/{addressId}")
    public ResponseEntity<?> adminUpdateAddress(@PathVariable Long addressId, @RequestBody AddressRequest request) {
        try { return ResponseEntity.ok(addressService.adminUpdateAddress(addressId, request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    @DeleteMapping("/admin/{addressId}")
    public ResponseEntity<?> adminDeleteAddress(@PathVariable Long addressId) {
        try {
            addressService.adminDeleteAddress(addressId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usunięto"));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }
}