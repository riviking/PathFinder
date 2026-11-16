package com.pathfinder.user.saved;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("saved_posts")
@Getter
@Setter
public class SavedPost {
    @Id
    private ObjectId id;
    private ObjectId studentId;
    private String noticeId;
    private Instant reminderAt;
    private Instant reminderSentAt;
    private String title;
    private String company;
    private Instant deadline;
    private Instant createdAt;
}