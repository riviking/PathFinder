package com.pathfinder.user.saved;

import java.time.Instant;

public record SavedResponse(String noticeId, String title, String company, Instant deadline,
                            Instant reminderAt, boolean reminderSent, Instant savedAt) {
    public static SavedResponse from(SavedPost p) {
        return new SavedResponse(p.getNoticeId(), p.getTitle(), p.getCompany(), p.getDeadline(),
                p.getReminderAt(), p.getReminderSentAt() != null, p.getCreatedAt());
    }
}