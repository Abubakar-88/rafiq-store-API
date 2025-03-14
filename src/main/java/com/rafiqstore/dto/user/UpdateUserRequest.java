package com.rafiqstore.dto.user;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private Set<String> roles; // Optional: Include this if roles are part of the update
}