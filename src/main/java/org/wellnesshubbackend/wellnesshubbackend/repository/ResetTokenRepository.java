package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wellnesshubbackend.wellnesshubbackend.model.ResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    Optional<ResetToken> findByToken(String token);
    void deleteAllByExpiresAtBefore(LocalDateTime time);
}
