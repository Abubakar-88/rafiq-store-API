package com.rafiqstore.repository;

import com.rafiqstore.entity.User;

public interface PasswordResetService {
   //void createPasswordResetTokenForUser(User user, String token);
    void sendPasswordResetEmail(String email);
    void resetPassword(String token, String newPassword);
}
