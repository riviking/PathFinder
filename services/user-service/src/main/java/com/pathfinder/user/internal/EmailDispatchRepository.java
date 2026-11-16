package com.pathfinder.user.internal;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailDispatchRepository extends MongoRepository<EmailDispatch, ObjectId> {
}