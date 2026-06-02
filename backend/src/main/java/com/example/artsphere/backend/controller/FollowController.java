package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.model.Artwork;
import com.example.artsphere.backend.model.SellerUserFollow;
import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.ArtworkRepository;
import com.example.artsphere.backend.repository.SellerUserFollowRepository;
import com.example.artsphere.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Kontroler REST do zarządzania obserwowaniem sprzedawców.
 *
 * @author ArtSphere Team
 */
@RestController
@RequestMapping("/api/follows")
public class FollowController {
    /**
     * Konstruktor domyślny.
     */
    public FollowController() {}

    @Autowired
    private SellerUserFollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    /**
     * Endpoint tworzący relację obserwowania sprzedawcy przez użytkownika.
     *
     * @param userId identyfikator użytkownika obserwującego.
     * @param sellerId identyfikator sprzedawcy, którego użytkownik chce obserwować.
     * @return informacja o utworzeniu relacji lub komunikat, że relacja już istnieje.
     */
    @PostMapping("/{userId}/{sellerId}")
    public ResponseEntity<?> follow(@PathVariable Long userId, @PathVariable Long sellerId) {
        if(followRepository.findByUserIdAndSellerId(userId, sellerId).isPresent()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Już obserwujesz"));
        }
        User user = userRepository.findById(userId).orElseThrow();
        User seller = userRepository.findById(sellerId).orElseThrow();

        SellerUserFollow follow = new SellerUserFollow();
        follow.setUser(user);
        follow.setSeller(seller);
        followRepository.save(follow);
        return ResponseEntity.ok(Collections.singletonMap("message", "Zaobserwowano"));
    }

    /**
     * Endpoint usuwający relację obserwowania sprzedawcy.
     *
     * @param userId identyfikator użytkownika obserwującego.
     * @param sellerId identyfikator sprzedawcy.
     * @return komunikat potwierdzający usunięcie relacji.
     */
    @DeleteMapping("/{userId}/{sellerId}")
    public ResponseEntity<?> unfollow(@PathVariable Long userId, @PathVariable Long sellerId) {
        followRepository.findByUserIdAndSellerId(userId, sellerId).ifPresent(followRepository::delete);
        return ResponseEntity.ok(Collections.singletonMap("message", "Odobserwowano"));
    }

    /**
     * Endpoint sprawdzający, czy użytkownik obserwuje wskazanego sprzedawcę.
     *
     * @param userId identyfikator użytkownika.
     * @param sellerId identyfikator sprzedawcy.
     * @return flaga logiczna informująca o relacji obserwowania.
     */
    @GetMapping("/{userId}/{sellerId}")
    public ResponseEntity<?> isFollowing(@PathVariable Long userId, @PathVariable Long sellerId) {
        boolean isFollowing = followRepository.findByUserIdAndSellerId(userId, sellerId).isPresent();
        return ResponseEntity.ok(Collections.singletonMap("isFollowing", isFollowing));
    }

    /**
     * Endpoint zwracający dostępne dzieła sprzedawców obserwowanych przez użytkownika.
     *
     * @param userId identyfikator użytkownika.
     * @return lista dostępnych dzieł obserwowanych sprzedawców.
     */
    @GetMapping("/{userId}/artworks")
    public ResponseEntity<?> getFollowedArtworks(@PathVariable Long userId) {
        List<Long> sellerIds = followRepository.findByUserId(userId).stream()
                .map(f -> f.getSeller().getId())
                .collect(Collectors.toList());

        if (sellerIds.isEmpty()) return ResponseEntity.ok(new ArrayList<>());

        List<Artwork> artworks = artworkRepository.findAll().stream()
                .filter(a -> sellerIds.contains(a.getUser().getId()) && "AVAILABLE".equals(a.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(artworks);
    }

    /**
     * Endpoint zwracający listę obserwujących danego sprzedawcy.
     *
     * @param sellerId identyfikator sprzedawcy.
     * @return lista podstawowych danych obserwujących użytkowników.
     */
    @GetMapping("/seller/{sellerId}/followers")
    public ResponseEntity<?> getSellerFollowers(@PathVariable Long sellerId) {
        List<User> followers = followRepository.findBySellerId(sellerId).stream()
                .map(SellerUserFollow::getUser)
                .collect(Collectors.toList());

        List<java.util.Map<String, Object>> result = followers.stream().map(u -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("email", u.getEmail());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}