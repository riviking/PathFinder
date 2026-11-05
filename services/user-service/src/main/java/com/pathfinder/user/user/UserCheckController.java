package com.pathfinder.user.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserCheckController {
    private final UserRepository users;

    public UserCheckController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/api/users/count")
    public long count() {
        return users.count();
    }

    @GetMapping("/api/users/emails")
    public List<String> emails() {
        return users.findAll().stream().map(User::getEmail).toList();
    }
}
