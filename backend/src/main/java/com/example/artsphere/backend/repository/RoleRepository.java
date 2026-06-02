package com.example.artsphere.backend.repository;

import com.example.artsphere.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repozytorium encji Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Znajduje rolę na podstawie jej nazwy.
     * @param name nazwa roli
     * @return opcjonalna rola
     */
    Optional<Role> findByName(String name);
}
