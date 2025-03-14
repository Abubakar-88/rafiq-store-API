package com.rafiqstore.config;

import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Date;


public class PasswordResetTokenUtil {

    // Default expiration time in minutes (e.g., 24 hours)
    @Value("${password.reset.token.expiration.minutes}")
    private int expirationTimeInMinutes;

    public Date calculateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTimeInMinutes); // Use configured value
        return new Date(calendar.getTime().getTime());
    }
}
