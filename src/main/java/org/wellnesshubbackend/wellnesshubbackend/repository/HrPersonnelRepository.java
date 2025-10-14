package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wellnesshubbackend.wellnesshubbackend.model.HrPersonnel;

import java.util.Optional;

public interface HrPersonnelRepository extends JpaRepository<HrPersonnel, Long> {
    Optional<HrPersonnel> findByUsername(String username);
    Optional<HrPersonnel> findByEmail(String email);
}
