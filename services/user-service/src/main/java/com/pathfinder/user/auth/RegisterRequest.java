package com.pathfinder.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "Enter your university email.")
        @Email(message = "Enter a valid email address.")
        String email,
        @NotBlank(message = "Enter a password.")
        @Size(min = 8, message = "Use at least 8 characters.")
        String password) {
    
}
