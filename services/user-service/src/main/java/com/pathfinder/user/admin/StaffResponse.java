package com.pathfinder.user.admin;

import com.pathfinder.user.user.User;

public record StaffResponse(String id, String name, String email, boolean active) {
    public static StaffResponse from(User u) {
        return new StaffResponse(u.getId().toString(), u.getName(), u.getEmail(), u.isActive());
    }
}