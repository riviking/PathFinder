package com.pathfinder.user;

import com.jayway.jsonpath.JsonPath;
import com.pathfinder.user.internal.EmailDispatchRepository;
import com.pathfinder.user.saved.SavedPostRepository;
import com.pathfinder.user.user.StudentProfileRepository;
import com.pathfinder.user.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FeatureApiTest {
    private static final String STUDENT_EMAIL = "junit.feature.student@example.com";
    private static final String STAFF_EMAIL = "junit.feature.staff@example.com";
    private static final String NOTICE_ID = "junit-internal-notice";
    private static final String SEED_PASSWORD = "ChangeMe123!";

    @Value("${app.internal-key}")
    String internalKey;

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired StudentProfileRepository profiles;
    @Autowired SavedPostRepository saved;
    @Autowired EmailDispatchRepository dispatches;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        users.findByEmail(STUDENT_EMAIL).ifPresent(u -> {
            saved.findByStudentIdOrderByCreatedAtDesc(u.getId()).forEach(saved::delete);
            profiles.findByUserId(u.getId()).ifPresent(profiles::delete);
            users.delete(u);
        });
        users.findByEmail(STAFF_EMAIL).ifPresent(users::delete);
        dispatches.findAll().stream()
                .filter(d -> NOTICE_ID.equals(d.getNoticeId()))
                .forEach(dispatches::delete);
    }

    private String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private String registerAndLogin() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Junit Feature\",\"email\":\"" + STUDENT_EMAIL
                                + "\",\"password\":\"Passw0rd!123\"}"))
                .andExpect(status().isCreated());
        return login(STUDENT_EMAIL, "Passw0rd!123");
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    // ---------- /internal (notice-service ඇමතුම) ----------

    @Test
    void internalEndpointNeedsTheKey() throws Exception {
        String body = "{\"noticeId\":\"" + NOTICE_ID + "\",\"title\":\"T\",\"categoryIds\":[\"no-such-category\"]}";
        mvc.perform(post("/internal/notices/published").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/internal/notices/published").header("X-Internal-Key", "wrong")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void internalEndpointRejectsEmptyCategories() throws Exception {
        mvc.perform(post("/internal/notices/published").header("X-Internal-Key", internalKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"noticeId\":\"" + NOTICE_ID + "\",\"title\":\"T\",\"categoryIds\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void internalCountForUnknownCategoryIsZeroAndCanBeRepeated() throws Exception {
        String body = "{\"noticeId\":\"" + NOTICE_ID + "\",\"title\":\"T\",\"categoryIds\":[\"no-such-category\"]}";
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/internal/notices/published").header("X-Internal-Key", internalKey)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.targetedCount").value(0));
        }
    }

    // ---------- profile ----------

    @Test
    void profileCategoriesAreTrimmedAndWithoutDuplicates() throws Exception {
        String token = registerAndLogin();
        mvc.perform(put("/api/profile/categories").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryIds\":[\"software-it\",\"software-it\",\" design \"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryIds.length()").value(2));
        mvc.perform(get("/api/profile").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryIds[0]").value("software-it"))
                .andExpect(jsonPath("$.categoryIds[1]").value("design"));
    }

    // ---------- saved posts ----------

    @Test
    void studentCanSaveAndUnsaveAPost() throws Exception {
        String token = registerAndLogin();
        mvc.perform(put("/api/saved/junit-notice-1").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Junit Intern\",\"company\":\"ABC\",\"reminderAt\":\"2099-01-01T09:00:00Z\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noticeId").value("junit-notice-1"))
                .andExpect(jsonPath("$.reminderSent").value(false));
        mvc.perform(get("/api/profile/saved").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(delete("/api/saved/junit-notice-1").header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/profile/saved").header("Authorization", bearer(token)))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void reminderInThePastIsRejected() throws Exception {
        String token = registerAndLogin();
        mvc.perform(put("/api/saved/junit-notice-2").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"T\",\"reminderAt\":\"2020-01-01T00:00:00Z\"}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- staff accounts ----------

    @Test
    void adminCanCreateAndDeactivateStaff() throws Exception {
        String admin = login("admin@example.com", SEED_PASSWORD);
        String staffJson = "{\"name\":\"Junit Staff\",\"email\":\"" + STAFF_EMAIL + "\",\"password\":\"Staff12345!\"}";

        String created = mvc.perform(post("/api/admin/staff").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(staffJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(created, "$.id");

        mvc.perform(post("/api/admin/staff").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content(staffJson))
                .andExpect(status().isConflict());

        mvc.perform(patch("/api/admin/staff/" + id + "/active").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + STAFF_EMAIL + "\",\"password\":\"Staff12345!\"}"))
                .andExpect(status().isForbidden());

        mvc.perform(patch("/api/admin/staff/" + id + "/active").header("Authorization", bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"active\":true}"))
                .andExpect(status().isOk());
        login(STAFF_EMAIL, "Staff12345!");
    }

    @Test
    void studentCannotCreateStaff() throws Exception {
        String student = login("student01@example.com", SEED_PASSWORD);
        mvc.perform(post("/api/admin/staff").header("Authorization", bearer(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"email\":\"" + STAFF_EMAIL + "\",\"password\":\"Staff12345!\"}"))
                .andExpect(status().isForbidden());
    }
}