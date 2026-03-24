package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    // Tu Spring automatycznie wygeneruje metody typu findAll(), findById(), save() itd.
}