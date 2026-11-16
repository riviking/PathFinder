package com.pathfinder.user.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PublishedNotice(
        @NotBlank String noticeId,
        @NotBlank String title,
        @NotEmpty List<String> categoryIds) {}