package com.pathfinder.user.internal;

import com.pathfinder.user.user.Role;
import com.pathfinder.user.user.StudentProfile;
import com.pathfinder.user.user.StudentProfileRepository;
import com.pathfinder.user.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InternalController {
    private final StudentProfileRepository profiles;
    private final UserRepository users;

    @PostMapping("/internal/notices/published")
    public Map<String, Integer> published(@Valid @RequestBody PublishedNotice n) {
        List<ObjectId> userIds = profiles.findBySelectedCategoryIdsIn(n.categoryIds()).stream()
                .map(StudentProfile::getUserId)
                .toList();
        long count = users.findAllById(userIds).stream()
                .filter(u -> u.getRole() == Role.STUDENT && u.isActive())
                .count();
        return Map.of("targetedCount", (int) count);
    }
}