package com.pathfinder.user.internal;

import com.pathfinder.user.user.Role;
import com.pathfinder.user.user.StudentProfile;
import com.pathfinder.user.user.StudentProfileRepository;
import com.pathfinder.user.user.User;
import com.pathfinder.user.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InternalController {
    private final StudentProfileRepository profiles;
    private final UserRepository users;
    private final EmailDispatchRepository dispatches;
    private final NoticeMailer mailer;

    @PostMapping("/internal/notices/published")
    public Map<String, Integer> published(@Valid @RequestBody PublishedNotice n) {
        List<ObjectId> userIds = profiles.findBySelectedCategoryIdsIn(n.categoryIds()).stream()
                .map(StudentProfile::getUserId)
                .toList();
        List<User> students = users.findAllById(userIds).stream()
                .filter(u -> u.getRole() == Role.STUDENT && u.isActive())
                .toList();

        EmailDispatch d = new EmailDispatch();
        d.setNoticeId(n.noticeId());
        d.setTitle(n.title());
        d.setTargetedCount(students.size());
        d.setStatus("SENDING");
        d.setCreatedAt(Instant.now());
        try {
            dispatches.insert(d);                       // noticeId unique: දෙවැනි වතාවට මෙතැනින් වැටේ
            mailer.sendNoticeEmails(d, students);       // පසුබිමේ යනවා
        } catch (DuplicateKeyException e) {
            // මේ notice එකේ emails කලින් යවලා: නැවත යවන්නේ නෑ
        }
        return Map.of("targetedCount", students.size());
    }
}