package com.pathfinder.user.saved;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SavedPostRepository extends MongoRepository<SavedPost, ObjectId> {
    List<SavedPost> findByStudentIdOrderByCreatedAtDesc(ObjectId studentId);
    Optional<SavedPost> findByStudentIdAndNoticeId(ObjectId studentId, String noticeId);
    long deleteByStudentIdAndNoticeId(ObjectId studentId, String noticeId);
}