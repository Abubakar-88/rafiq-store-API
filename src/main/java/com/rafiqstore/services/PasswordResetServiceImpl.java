package com.rafiqstore.services;

import com.rafiqstore.config.PasswordResetTokenUtil;
import com.rafiqstore.entity.PasswordResetToken;
import com.rafiqstore.entity.User;
import com.rafiqstore.exception.ResourceNotFoundException;
import com.rafiqstore.repository.PasswordResetService;
import com.rafiqstore.repository.PasswordResetTokenRepository;
import com.rafiqstore.repository.UserRepository;
import com.rafiqstore.services.serviceImpl.ArticleServiceImpl;
import jakarta.transaction.Transactional;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.baseUrl}")
    private String baseUrl;

    @Value("${password.reset.token.expiration.minutes}")
    private int expirationTimeInMinutes;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    @Override
    @Transactional
    public void sendPasswordResetEmail(String email) {
        // Normalize the email
        email = email.trim().toLowerCase();
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        // Debug: Log the email being searched
        System.out.println("Searching for user with email: " + email);

        // Find the user by email (case-insensitive)
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isEmpty()) {
            // Debug: Log the error
            System.err.println("User not found with email: " + email);
            throw new ResourceNotFoundException("No user found with the provided email.");
        }

        User user = userOptional.get();
        System.out.println("User found: " + user.getEmail());

        // Generate a unique token
        String token = UUID.randomUUID().toString();

        // Create or update the password reset token
        createOrUpdatePasswordResetToken(user, token);

        // Generate the reset link
        String resetLink = baseUrl + "/admin/reset-password?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        // Prepare the email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("contact@voroshait.com"); // Set a valid sender address
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Password Reset Request");
        mailMessage.setText("To reset your password, click the link below:\n" + resetLink);

        // Send the email
        try {
            mailSender.send(mailMessage);
            System.out.println("Password reset email sent to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private void createOrUpdatePasswordResetToken(User user, String token) {
        // Check if a token already exists for the user
        PasswordResetToken existingToken = passwordResetTokenRepository.findByUser(user).orElse(null);

        if (existingToken != null) {
            // Update the existing token
            existingToken.setToken(token);
            existingToken.setExpiryDate(calculateExpiryDate());
            passwordResetTokenRepository.save(existingToken);
        } else {
            // Create a new token
            PasswordResetToken newToken = new PasswordResetToken(token, user);
            newToken.setExpiryDate(calculateExpiryDate());
            passwordResetTokenRepository.save(newToken);
        }
    }

    private Date calculateExpiryDate() {
        // Set token expiration time (e.g., 24 hours)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.HOUR, 24); // 24 hours validity
        return new Date(calendar.getTime().getTime());
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Validate the new password
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Find the token in the database
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        // Check if the token has expired
        if (resetToken.getExpiryDate().before(new Date())) {
            throw new IllegalArgumentException("Token has expired");
        }

        // Update the user's password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Log the successful password reset
        logger.info("Password reset successfully for user: " + user.getEmail());

        // Delete the token (gracefully handle errors)
        try {
            passwordResetTokenRepository.delete(resetToken);
        } catch (Exception e) {
            logger.error("Failed to delete password reset token: " + e.getMessage());
        }
    }
}
