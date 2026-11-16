package com.pathfinder.user.profile;

import com.pathfinder.user.user.StudentProfile;
import com.pathfinder.user.user.StudentProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final StudentProfileRepository profiles;

    @GetMapping
    public ProfileResponse get(Authentication auth) {
        return ProfileResponse.from(find(auth));
    }

    @PutMapping("/categories")
    public ProfileResponse setCategories(Authentication auth, @Valid @RequestBody CategoriesRequest req) {
        StudentProfile p = find(auth);
        p.setSelectedCategoryIds(req.categoryIds().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList());
        p.setUpdatedAt(Instant.now());
        profiles.save(p);
        return ProfileResponse.from(p);
    }

    private StudentProfile find(Authentication auth) {
        return profiles.findByUserId(new ObjectId(auth.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found."));
    }
}