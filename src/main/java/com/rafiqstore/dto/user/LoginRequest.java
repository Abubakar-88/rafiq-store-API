package com.rafiqstore.dto.user;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}