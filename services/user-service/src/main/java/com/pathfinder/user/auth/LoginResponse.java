package com.pathfinder.user.auth;

public record LoginResponse(String token,String userId, String email, String name, String role) {
}
