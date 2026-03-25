package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AddressRequest;
import com.example.artsphere.backend.dto.AddressResponse;
import com.example.artsphere.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    // Pobierz adresy użytkownika
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable Long userId) {
        try {
            List<AddressResponse> addresses = addressService.getUserAddresses(userId);
            return ResponseEntity.ok(addresses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Dodaj nowy adres
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAddress(@PathVariable Long userId, @RequestBody AddressRequest request) {
        try {
            AddressResponse address = addressService.createAddress(userId, request);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Aktualizuj adres
    @PutMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long addressId,
            @PathVariable Long userId,
            @RequestBody AddressRequest request) {
        try {
            AddressResponse address = addressService.updateAddress(addressId, userId, request);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Usuń adres
    @DeleteMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId, @PathVariable Long userId) {
        try {
            addressService.deleteAddress(addressId, userId);
            return ResponseEntity.ok("Adres usunięty pomyślnie");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin - wszystkie adresy
    @GetMapping("/admin/all")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        try {
            List<AddressResponse> addresses = addressService.getAllAddresses();
            return ResponseEntity.ok(addresses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Admin - pobierz pojedynczy adres
    @GetMapping("/admin/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long addressId) {
        try {
            AddressResponse address = addressService.getAddressById(addressId);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Admin - edytuj adres dowolnego użytkownika
    @PutMapping("/admin/{addressId}")
    public ResponseEntity<?> adminUpdateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressRequest request) {
        try {
            AddressResponse address = addressService.adminUpdateAddress(addressId, request);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin - usuń adres dowolnego użytkownika
    @DeleteMapping("/admin/{addressId}")
    public ResponseEntity<?> adminDeleteAddress(@PathVariable Long addressId) {
        try {
            addressService.adminDeleteAddress(addressId);
            return ResponseEntity.ok("Adres usunięty pomyślnie");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
