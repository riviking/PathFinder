package com.pathfinder.user.admin;

import com.pathfinder.user.user.Role;
import com.pathfinder.user.user.User;
import com.pathfinder.user.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
public class StaffController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> create(@Valid @RequestBody CreateStaffRequest req) {
        if (users.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered.");
        }
        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.STAFF);
        u.setActive(true);
        users.save(u);
        return Map.of("id", u.getId().toString(), "email", u.getEmail(), "role", "STAFF");
    }
    @GetMapping
    public List<StaffResponse> list() {
        return users.findByRole(Role.STAFF).stream().map(StaffResponse::from).toList();
    }

    @PatchMapping("/{id}/active")
    public StaffResponse setActive(@PathVariable String id, @RequestBody Map<String, Boolean> body) {
        Boolean active = body.get("active");
        if (active == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Send {\"active\": true} or {\"active\": false}.");
        }
        User u = users.findById(new ObjectId(id))
                .filter(x -> x.getRole() == Role.STAFF)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff account not found."));
        u.setActive(active);
        users.save(u);
        return StaffResponse.from(u);
    }


}