package com.pathfinder.user.user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends MongoRepository<StudentProfile, ObjectId> {
    Optional<StudentProfile> findByUserId(ObjectId userId);
    List<StudentProfile> findBySelectedCategoryIdsIn(java.util.Collection<String> ids);
}
