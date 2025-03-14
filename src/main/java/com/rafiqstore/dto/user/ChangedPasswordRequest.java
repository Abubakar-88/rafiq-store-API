package com.rafiqstore.dto.user;

import lombok.Data;

@Data
public class ChangedPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
