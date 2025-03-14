package com.rafiqstore.repository;

import com.rafiqstore.entity.PasswordResetToken;
import com.rafiqstore.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);
    @Transactional
    void deleteByUser(User user);
}
