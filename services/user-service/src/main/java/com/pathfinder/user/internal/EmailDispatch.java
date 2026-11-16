package com.pathfinder.user.internal;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("email_dispatches")
@Getter
@Setter
public class EmailDispatch {
    @Id
    private ObjectId id;
    private String noticeId;
    private String title;
    private int targetedCount;
    private int sentCount;
    private int failedCount;
    private String status;        // SENDING, DONE
    private Instant createdAt;
    private Instant finishedAt;
}