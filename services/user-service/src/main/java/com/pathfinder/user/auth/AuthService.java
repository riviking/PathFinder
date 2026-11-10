package com.pathfinder.user.auth;

import com.pathfinder.user.security.JwtService;
import com.pathfinder.user.user.Role;
import com.pathfinder.user.user.StudentProfile;
import com.pathfinder.user.user.StudentProfileRepository;
import com.pathfinder.user.user.User;
import com.pathfinder.user.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final StudentProfileRepository profiles;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public void register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(email.substring(0, email.indexOf('@')));
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRole(Role.STUDENT);
        user.setActive(true);
        user.setCreatedAt(Instant.now());
        user = users.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUserId(user.getId());
        profile.setUpdatedAt(Instant.now());
        profiles.save(profile);
    }

    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        User user = users.findByEmail(email)
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong email or password."));
        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account is not active.");
        }
        user.setLastLoginAt(Instant.now());
        users.save(user);
        String token = jwt.create(user.getId().toHexString(), user.getRole().name(), user.getEmail(), user.getName());
        return new LoginResponse(token,user.getId().toHexString(), user.getEmail(), user.getName(), user.getRole().name());
    }
}
