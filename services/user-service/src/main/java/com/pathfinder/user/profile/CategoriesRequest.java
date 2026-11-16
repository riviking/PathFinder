package com.pathfinder.user.profile;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoriesRequest(@NotNull List<String> categoryIds) {}