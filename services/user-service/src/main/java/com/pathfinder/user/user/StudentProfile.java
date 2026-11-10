package com.pathfinder.user.user;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("student_profiles")
@Getter
@Setter
public class StudentProfile {
    @Id
    private ObjectId id;
    @Indexed(name = "userId_1", unique = true)
    private ObjectId userId;
    private List<String> selectedCategoryIds = new ArrayList<>();   // job category slugs, e.g. "software-it"
    private Instant updatedAt;
}

