package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.dto.*;
import com.example.artsphere.backend.model.*;
import com.example.artsphere.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "*")
public class SupportController {

    @Autowired private UserRepository userRepository;
    @Autowired private DonationRepository donationRepository;
    @Autowired private WalletTransactionRepository walletTransactionRepository;

    @GetMapping("/artists")
    public ResponseEntity<List<ArtistDto>> getArtists() {
        List<ArtistDto> artists = userRepository.findByRole("ARTIST").stream()
                .map(u -> new ArtistDto(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(artists);
    }

    @PostMapping("/donate")
    @Transactional
    public ResponseEntity<?> donate(@RequestBody DonationRequest req) {
        User client = userRepository.findById(req.getClientId()).orElseThrow();
        User seller = userRepository.findById(req.getSellerId()).orElseThrow();
        BigDecimal amount = BigDecimal.valueOf(req.getAmount());

        if (client.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Brak wystarczających środków"));
        }

        // Zmiana sald (pobieranie od kupującego, dawanie artyście)
        client.setBalance(client.getBalance().subtract(amount));
        seller.setBalance(seller.getBalance().add(amount));
        userRepository.save(client);
        userRepository.save(seller);

        // Zapis dla ekranu Wesprzyj (tabela donations)
        Donation d = new Donation();
        d.setClient(client);
        d.setSeller(seller);
        d.setAmount(amount);
        donationRepository.save(d);

        // Zapis do portfela kupującego
        WalletTransaction wtClient = new WalletTransaction();
        wtClient.setUser(client);
        wtClient.setTitle("Wsparcie dla: " + seller.getUsername());
        wtClient.setAmount(amount);
        wtClient.setIncome(false);
        walletTransactionRepository.save(wtClient);

        // Zapis do portfela artysty
        WalletTransaction wtSeller = new WalletTransaction();
        wtSeller.setUser(seller);
        wtSeller.setTitle("Otrzymano wsparcie od: " + client.getUsername());
        wtSeller.setAmount(amount);
        wtSeller.setIncome(true);
        walletTransactionRepository.save(wtSeller);

        // Zwracamy nowe saldo dla odświeżenia w aplikacji
        return ResponseEntity.ok(Collections.singletonMap("newBalance", client.getBalance().doubleValue()));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<DonationHistoryResponse>> getHistory(@PathVariable Long userId) {
        List<DonationHistoryResponse> history = donationRepository.findByClientIdOrderByIdDesc(userId).stream()
                .map(d -> new DonationHistoryResponse(
                        d.getSeller().getUsername(),
                        d.getAmount().doubleValue()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }
}