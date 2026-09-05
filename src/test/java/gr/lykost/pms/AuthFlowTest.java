package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthFlowTest extends AbstractIntegrationTest {

    private static final String ADMIN_LOGIN = """
            {"username":"admin","password":"admin"}""";

    @Test
    void loginWithValidCredentialsReturnsAuthUser() throws Exception {
        mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(APPLICATION_JSON).content(ADMIN_LOGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.systemRole").value("ADMIN"))
                .andExpect(jsonPath("$.fullName").value("System Administrator"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"wrong"}"""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meWithoutSessionReturns401AndDoesNotRedirect() throws Exception {
        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String location = result.getResponse().getHeader("Location");
                    if (location != null) {
                        throw new AssertionError("Expected no redirect, got Location: " + location);
                    }
                });
    }

    @Test
    void meWithSessionReturnsCurrentUser() throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(APPLICATION_JSON).content(ADMIN_LOGIN))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void logoutEndsTheSession() throws Exception {
        MvcResult login = mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(APPLICATION_JSON).content(ADMIN_LOGIN))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mvc.perform(post("/api/auth/logout").with(csrf()).session(session))
                .andExpect(status().isOk());

        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }
}
