package org.wellnesshubbackend.wellnesshubbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wellnesshubbackend.wellnesshubbackend.model.EmailVerificationToken;
import org.wellnesshubbackend.wellnesshubbackend.model.User;
import org.wellnesshubbackend.wellnesshubbackend.repository.EmailVerificationTokenRepository;
import org.wellnesshubbackend.wellnesshubbackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public EmailVerificationToken createVerificationToken(User user) {
        // Supprimer les anciens tokens pour cet utilisateur
        tokenRepository.deleteByUser(user);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(24)); // Token valide 24h
        token.setUsed(false);

        return tokenRepository.save(token);
    }

    public Optional<EmailVerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Transactional
    public void markTokenAsUsed(EmailVerificationToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }

    public boolean isValidToken(String token) {
        Optional<EmailVerificationToken> verificationToken = findByToken(token);
        return verificationToken.isPresent() && verificationToken.get().isValid();
    }

    public Optional<EmailVerificationToken> findByUserEmail(String email) {
        // Chercher l'utilisateur par email puis son token
        return tokenRepository.findAll().stream()
                .filter(token -> token.getUser().getEmail().equals(email))
                .findFirst();
    }

    @Transactional
    public void markUserAsVerified(User user) {
        user.setUserVerified(true);
        userRepository.save(user);
    }
}
