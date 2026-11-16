package com.pathfinder.user.saved;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record SaveRequest(
        @NotBlank String title,
        String company,
        Instant deadline,
        Instant reminderAt) {}