package com.pathfinder.user.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
}
