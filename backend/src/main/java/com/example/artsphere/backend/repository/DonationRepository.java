package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByClientIdOrderByIdDesc(Long clientId);
}