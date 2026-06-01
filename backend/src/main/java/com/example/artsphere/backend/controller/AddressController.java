package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.AddressRequest;
import com.example.artsphere.backend.dto.AddressResponse;
import com.example.artsphere.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;

/**
 * Kontroler REST do zarządzania adresami użytkowników.
 */
@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * Endpoint zwracający listę adresów przypisanych do użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @return lista adresów użytkownika.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    /**
     * Endpoint tworzący nowy adres dla użytkownika.
     *
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param request dane adresu przekazane w ciele żądania.
     * @return nowo utworzony adres lub błąd walidacji.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAddress(@PathVariable Long userId, @RequestBody AddressRequest request) {
        try { return ResponseEntity.ok(addressService.createAddress(userId, request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint aktualizujący adres należący do użytkownika.
     *
     * @param addressId identyfikator adresu w ścieżce URL.
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @param request nowe dane adresu przekazane w ciele żądania.
     * @return zaktualizowany adres lub błąd walidacji.
     */
    @PutMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId, @PathVariable Long userId, @RequestBody AddressRequest request) {
        try { return ResponseEntity.ok(addressService.updateAddress(addressId, userId, request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint usuwający adres użytkownika.
     *
     * @param addressId identyfikator adresu w ścieżce URL.
     * @param userId identyfikator użytkownika w ścieżce URL.
     * @return komunikat potwierdzający usunięcie lub błąd walidacji.
     */
    @DeleteMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId, @PathVariable Long userId) {
        try {
            addressService.deleteAddress(addressId, userId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usunięto"));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint administracyjny zwracający wszystkie adresy w systemie.
     *
     * @return lista wszystkich adresów.
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<AddressResponse>> getAllAddresses() { return ResponseEntity.ok(addressService.getAllAddresses()); }

    /**
     * Endpoint zwracający pojedynczy adres po identyfikatorze.
     *
     * @param id identyfikator adresu w ścieżce URL.
     * @return adres w formacie {@link AddressResponse}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) { return ResponseEntity.ok(addressService.getAddressById(id)); }

    /**
     * Endpoint administracyjny do aktualizacji adresu.
     *
     * @param addressId identyfikator adresu w ścieżce URL.
     * @param request nowe dane adresu.
     * @return zaktualizowany adres lub błąd walidacji.
     */
    @PutMapping("/admin/{addressId}")
    public ResponseEntity<?> adminUpdateAddress(@PathVariable Long addressId, @RequestBody AddressRequest request) {
        try { return ResponseEntity.ok(addressService.adminUpdateAddress(addressId, request)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }

    /**
     * Endpoint administracyjny do usunięcia adresu.
     *
     * @param addressId identyfikator adresu w ścieżce URL.
     * @return komunikat potwierdzający usunięcie lub błąd walidacji.
     */
    @DeleteMapping("/admin/{addressId}")
    public ResponseEntity<?> adminDeleteAddress(@PathVariable Long addressId) {
        try {
            addressService.adminDeleteAddress(addressId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usunięto"));
        } catch (RuntimeException e) { return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage())); }
    }
}