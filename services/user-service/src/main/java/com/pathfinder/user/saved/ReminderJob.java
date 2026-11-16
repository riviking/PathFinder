package com.pathfinder.user.saved;

import com.pathfinder.user.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderJob {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a").withZone(ZoneId.of("Asia/Colombo"));

    private final SavedPostRepository saved;
    private final UserRepository users;
    private final JavaMailSender mailSender;

    @Scheduled(fixedDelay = 60_000)
    public void sendDueReminders() {
        for (SavedPost p : saved.findByReminderAtLessThanEqualAndReminderSentAtIsNull(Instant.now())) {
            users.findById(p.getStudentId()).ifPresent(u -> {
                try {
                    if (u.isActive()) {
                        SimpleMailMessage m = new SimpleMailMessage();
                        m.setFrom("no-reply@pathfinder.local");
                        m.setTo(u.getEmail());
                        m.setSubject("Reminder: " + p.getTitle());
                        m.setText("Hi " + u.getName() + ",\n\nThis is your reminder for \"" + p.getTitle() + "\""
                                + (p.getCompany() != null ? " at " + p.getCompany() : "")
                                + (p.getDeadline() != null ? ".\nApplication deadline: " + FMT.format(p.getDeadline()) : "")
                                + ".\n\nCareer Guidance Unit");
                        mailSender.send(m);
                    }
                    p.setReminderSentAt(Instant.now());
                    saved.save(p);
                } catch (Exception e) {
                    log.warn("Reminder to {} failed: {}", u.getEmail(), e.getMessage());   // ඊළඟ විනාඩියේ නැවත උත්සාහ
                }
            });
        }
    }
}