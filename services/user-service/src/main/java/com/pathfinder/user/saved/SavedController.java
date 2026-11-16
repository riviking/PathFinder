package com.pathfinder.user.saved;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SavedController {
    private final SavedPostRepository saved;

    @PutMapping("/api/saved/{noticeId}")
    public SavedResponse save(Authentication auth, @PathVariable String noticeId,
                              @Valid @RequestBody SaveRequest req) {
        if (req.reminderAt() != null && req.reminderAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reminder time must be in the future.");
        }
        ObjectId me = new ObjectId(auth.getName());
        SavedPost p = saved.findByStudentIdAndNoticeId(me, noticeId).orElseGet(() -> {
            SavedPost n = new SavedPost();
            n.setStudentId(me);
            n.setNoticeId(noticeId);
            n.setCreatedAt(Instant.now());
            return n;
        });
        p.setTitle(req.title());
        p.setCompany(req.company());
        p.setDeadline(req.deadline());
        p.setReminderAt(req.reminderAt());
        p.setReminderSentAt(null);              // reminder වෙලාව වෙනස් වුණොත් නැවත යවන්න
        saved.save(p);
        return SavedResponse.from(p);
    }

    @DeleteMapping("/api/saved/{noticeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsave(Authentication auth, @PathVariable String noticeId) {
        saved.deleteByStudentIdAndNoticeId(new ObjectId(auth.getName()), noticeId);
    }

    @GetMapping("/api/profile/saved")
    public List<SavedResponse> mine(Authentication auth) {
        return saved.findByStudentIdOrderByCreatedAtDesc(new ObjectId(auth.getName()))
                .stream().map(SavedResponse::from).toList();
    }
}