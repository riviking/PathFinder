package com.pathfinder.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {
    private static final String SECRET = "test-secret-test-secret-test-secret-1234";
    private final JwtService jwt = new JwtService(SECRET, 60);

    @Test
    void createdTokenCanBeReadBack() {
        String token = jwt.create("u1", "STAFF", "amal@example.com", "Amal");
        Claims c = jwt.parse(token);
        assertEquals("u1", c.getSubject());
        assertEquals("STAFF", c.get("role", String.class));
        assertEquals("amal@example.com", c.get("email", String.class));
        assertEquals("Amal", c.get("name", String.class));
    }

    @Test
    void fakeTokenIsRejected() {
        assertThrows(JwtException.class, () -> jwt.parse("abc.def.ghi"));
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService other = new JwtService("another-secret-another-secret-another-secret-99", 60);
        String token = other.create("u1", "ADMIN", "x@example.com", "X");
        assertThrows(JwtException.class, () -> jwt.parse(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService shortLived = new JwtService(SECRET, -1);      // අවසන් වේලාව දැනටමත් ඉකුත්
        String token = shortLived.create("u1", "STUDENT", "s@example.com", "S");
        assertThrows(JwtException.class, () -> jwt.parse(token));
    }
}