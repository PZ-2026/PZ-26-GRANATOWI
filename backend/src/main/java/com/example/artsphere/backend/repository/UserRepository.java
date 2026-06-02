package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Repozytorium encji User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Znajduje użytkownika na podstawie nazwy użytkownika.
     * @param username nazwa użytkownika
     * @return opcjonalny użytkownik
     */
    Optional<User> findByUsername(String username);

    /**
     * Znajduje użytkownika na podstawie adresu e-mail.
     * @param email adres e-mail
     * @return opcjonalny użytkownik
     */
    Optional<User> findByEmail(String email);

    /**
     * Znajduje użytkowników o określonej roli.
     * @param role nazwa roli
     * @return lista użytkowników
     */
    List<User> findByRole(String role);
}