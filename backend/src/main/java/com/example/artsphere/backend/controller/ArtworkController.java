package com.example.artsphere.backend.controller;

import com.example.artsphere.backend.model.Artwork;
import com.example.artsphere.backend.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artworks") // Pod tym adresem będzie dostępne API
public class ArtworkController {

    @Autowired
    private ArtworkRepository artworkRepository;

    // Pobieranie wszystkich dzieł sztuki
    @GetMapping
    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }
}