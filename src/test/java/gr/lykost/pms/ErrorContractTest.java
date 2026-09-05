package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import gr.lykost.pms.support.DomainFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ErrorContractTest extends AbstractIntegrationTest {

    private MockHttpSession session;

    @BeforeEach
    void signIn() throws Exception {
        session = DomainFixtures.adminSession(mvc);
    }

    @Test
    void validationFailureReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/business-roles").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"description":"no name"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.name").isNotEmpty());
    }

    @Test
    void unauthenticatedReturns401() throws Exception {
        mvc.perform(get("/api/departments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void missingResourceReturns404WithApiErrorShape() throws Exception {
        mvc.perform(get("/api/departments/99999999").session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void duplicateReturns409WithApiErrorShape() throws Exception {
        String body = """
                {"name":"Unique Role Name","description":"d"}""";

        mvc.perform(post("/api/business-roles").with(csrf()).session(session)
                .contentType(APPLICATION_JSON).content(body)).andExpect(status().isCreated());

        mvc.perform(post("/api/business-roles").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").isNotEmpty());
    }
}
