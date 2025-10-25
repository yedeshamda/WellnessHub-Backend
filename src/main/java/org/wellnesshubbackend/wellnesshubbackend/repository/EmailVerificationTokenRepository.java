package org.wellnesshubbackend.wellnesshubbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.User;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser(User user);
    void deleteByUser(User user);
}
