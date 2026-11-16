package com.pathfinder.user.profile;

import com.pathfinder.user.user.StudentProfile;

import java.util.List;

public record ProfileResponse(String userId, List<String> categoryIds) {
    public static ProfileResponse from(StudentProfile p) {
        List<String> ids = p.getSelectedCategoryIds() == null ? List.of() : p.getSelectedCategoryIds();
        return new ProfileResponse(p.getUserId().toString(), ids);
    }
}