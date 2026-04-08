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

@RestController
@RequestMapping("/api/follows")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private SellerUserFollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

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

    @DeleteMapping("/{userId}/{sellerId}")
    public ResponseEntity<?> unfollow(@PathVariable Long userId, @PathVariable Long sellerId) {
        followRepository.findByUserIdAndSellerId(userId, sellerId).ifPresent(followRepository::delete);
        return ResponseEntity.ok(Collections.singletonMap("message", "Odobserwowano"));
    }

    @GetMapping("/{userId}/{sellerId}")
    public ResponseEntity<?> isFollowing(@PathVariable Long userId, @PathVariable Long sellerId) {
        boolean isFollowing = followRepository.findByUserIdAndSellerId(userId, sellerId).isPresent();
        return ResponseEntity.ok(Collections.singletonMap("isFollowing", isFollowing));
    }

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