package com.pathfinder.user.user;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/api/users/me")
    public Map<String, String> me(Authentication auth) {
        Claims c = (Claims) auth.getDetails();
        return Map.of(
                "id", c.getSubject(),
                "email", c.get("email", String.class),
                "name", c.get("name", String.class),
                "role", c.get("role", String.class));
    }
}