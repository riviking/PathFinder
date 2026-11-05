package com.pathfinder.user.user;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("users")
@Getter
@Setter
public class User {
    @Id
    private ObjectId id;
    @Indexed(name = "email_1", unique = true)
    private String email;
    private String passwordHash;
    private Role role;
    private String name;
    private boolean active = true;
    private Instant createdAt;
    private Instant lastLoginAt;
}