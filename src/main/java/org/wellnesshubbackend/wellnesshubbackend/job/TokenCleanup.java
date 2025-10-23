package org.wellnesshubbackend.wellnesshubbackend.job;


import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.wellnesshubbackend.wellnesshubbackend.repository.ResetTokenRepository;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
public class TokenCleanup {
    private final ResetTokenRepository resetTokenRepository;

    public TokenCleanup(ResetTokenRepository resetTokenRepository) {
        this.resetTokenRepository = resetTokenRepository;
    }
    @Transactional
    @Scheduled(fixedRate = 3_600_000) // runs every hour
    public void clearExpiredTokens() {
        resetTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
