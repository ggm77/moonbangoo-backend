package com.seohamin.moonbangoo.domain.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminAuthTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession login() throws Exception {
        final MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/v1/admin/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"password":"test-password"}
                                """))
                .andExpect(status().isNoContent());
        return session;
    }

    @Test
    void 로그인하지_않으면_어드민_API가_401이다() throws Exception {
        mockMvc.perform(get("/api/v1/admin/packs"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ADMIN_UNAUTHORIZED"));

        mockMvc.perform(post("/api/v1/admin/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"confirm":true}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 비밀번호가_맞으면_어드민_API를_쓸_수_있다() throws Exception {
        final MockHttpSession session = login();

        mockMvc.perform(get("/api/v1/admin/packs").session(session))
                .andExpect(status().isOk());
    }

    @Test
    void 비밀번호가_틀리면_401이고_로그인되지_않는다() throws Exception {
        final MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/v1/admin/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_ADMIN_PASSWORD"));

        mockMvc.perform(get("/api/v1/admin/packs").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 비밀번호가_비어_있으면_400이다() throws Exception {
        mockMvc.perform(post("/api/v1/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void 로그아웃하면_다시_401이다() throws Exception {
        final MockHttpSession session = login();

        mockMvc.perform(post("/api/v1/admin/logout").session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admin/packs").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인하지_않아도_로그아웃은_204이다() throws Exception {
        mockMvc.perform(post("/api/v1/admin/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void 손님용_API는_로그인_없이_쓸_수_있다() throws Exception {
        mockMvc.perform(get("/api/v1/packs"))
                .andExpect(status().isOk());
    }

    @Test
    void CORS_사전_요청은_로그인_없이_통과한다() throws Exception {
        mockMvc.perform(options("/api/v1/admin/packs")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }
}
