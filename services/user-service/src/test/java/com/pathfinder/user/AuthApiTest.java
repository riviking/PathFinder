package com.pathfinder.user;

import com.jayway.jsonpath.JsonPath;
import com.pathfinder.user.user.StudentProfileRepository;
import com.pathfinder.user.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiTest {
    private static final String NEW_EMAIL = "junit.student@example.com";
    private static final String SEED_PASSWORD = "ChangeMe123!";

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired StudentProfileRepository profiles;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        users.findByEmail(NEW_EMAIL).ifPresent(u -> {
            profiles.findByUserId(u.getId()).ifPresent(profiles::delete);
            users.delete(u);
        });
    }

    private String json(String... pairs) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(pairs[i]).append("\":\"").append(pairs[i + 1]).append("\"");
        }
        return sb.append("}").toString();
    }

    private String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("email", email, "password", password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    @Test
    void registerThenLoginAsStudent() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json("name", "Junit Student", "email", NEW_EMAIL, "password", "Passw0rd!123")))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("email", NEW_EMAIL, "password", "Passw0rd!123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void registerWithExistingEmailGives409() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json("name", "Dup", "email", "student01@example.com", "password", "Passw0rd!123")))
                .andExpect(status().isConflict());
    }

    @Test
    void registerWithShortPasswordGives400() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json("name", "Short", "email", NEW_EMAIL, "password", "abc")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithWrongPasswordGives401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("email", "student01@example.com", "password", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithoutTokenGives401() throws Exception {
        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void meWithFakeTokenGives401() throws Exception {
        mvc.perform(get("/api/users/me").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithValidTokenReturnsMyDetails() throws Exception {
        String token = login("student01@example.com", SEED_PASSWORD);
        mvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student01@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void studentCannotOpenAdminArea() throws Exception {
        String token = login("student01@example.com", SEED_PASSWORD);
        mvc.perform(get("/api/admin/staff").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListStaff() throws Exception {
        String token = login("admin@example.com", SEED_PASSWORD);
        mvc.perform(get("/api/admin/staff").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}