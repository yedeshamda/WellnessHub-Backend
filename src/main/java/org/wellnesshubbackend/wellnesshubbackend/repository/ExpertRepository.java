package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wellnesshubbackend.wellnesshubbackend.model.Expert;

import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

    Optional<Expert> findByUsername(String username);
    Optional<Expert> findByEmail(String email);

}

