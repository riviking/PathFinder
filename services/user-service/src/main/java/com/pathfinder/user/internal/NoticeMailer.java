package com.pathfinder.user.internal;

import com.pathfinder.user.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeMailer {
    private final JavaMailSender mailSender;
    private final EmailDispatchRepository dispatches;

    @Async
    public void sendNoticeEmails(EmailDispatch d, List<User> students) {
        int sent = 0;
        int failed = 0;
        for (User s : students) {
            try {
                SimpleMailMessage m = new SimpleMailMessage();
                m.setFrom("no-reply@pathfinder.local");
                m.setTo(s.getEmail());
                m.setSubject("New internship: " + d.getTitle());
                m.setText("Hi " + s.getName() + ",\n\nA new internship matching your interests was posted: "
                        + d.getTitle() + ".\nLog in to PathFinder to view it.\n\nCareer Guidance Unit");
                mailSender.send(m);
                sent++;
            } catch (Exception e) {
                log.warn("Email to {} failed: {}", s.getEmail(), e.getMessage());
                failed++;
            }
        }
        d.setSentCount(sent);
        d.setFailedCount(failed);
        d.setStatus("DONE");
        d.setFinishedAt(Instant.now());
        dispatches.save(d);
    }
}