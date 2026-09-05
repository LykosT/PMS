package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleAccessTest extends AbstractIntegrationTest {

    private static final List<String> RESOURCES = List.of(
            "employees", "departments", "projects", "teams", "tasks", "business-roles");

    record Case(String method, String path, String role, boolean forbidden) {
        @Override public String toString() {
            return "%s /api/%s as %s -> %s".formatted(method, path, role, forbidden ? "403" : "not 403");
        }
    }

    static Stream<Case> matrix() {
        return RESOURCES.stream().flatMap(r -> Stream.of(
                // list: all three roles allowed
                new Case("GET", r, "ADMIN", false),
                new Case("GET", r, "MANAGER", false),
                new Case("GET", r, "USER", false),
                // get by id: USER denied
                new Case("GET", r + "/1", "ADMIN", false),
                new Case("GET", r + "/1", "MANAGER", false),
                new Case("GET", r + "/1", "USER", true),
                // create: USER denied
                new Case("POST", r, "ADMIN", false),
                new Case("POST", r, "MANAGER", false),
                new Case("POST", r, "USER", true),
                // update: USER denied
                new Case("PUT", r + "/1", "ADMIN", false),
                new Case("PUT", r + "/1", "MANAGER", false),
                new Case("PUT", r + "/1", "USER", true),
                // delete: ADMIN only
                new Case("DELETE", r + "/1", "ADMIN", false),
                new Case("DELETE", r + "/1", "MANAGER", true),
                new Case("DELETE", r + "/1", "USER", true)
        ));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("matrix")
    void enforcesTheDocumentedMatrix(Case testCase) throws Exception {
        RequestBuilder request = switch (testCase.method()) {
            case "GET" -> get("/api/" + testCase.path())
                    .with(user(testCase.role()));
            case "POST" -> post("/api/" + testCase.path()).with(csrf())
                    .with(user(testCase.role())).contentType(APPLICATION_JSON).content("{}");
            case "PUT" -> put("/api/" + testCase.path()).with(csrf())
                    .with(user(testCase.role())).contentType(APPLICATION_JSON).content("{}");
            case "DELETE" -> delete("/api/" + testCase.path()).with(csrf())
                    .with(user(testCase.role()));
            default -> throw new IllegalArgumentException(testCase.method());
        };

        var result = mvc.perform(request).andReturn();
        int actual = result.getResponse().getStatus();

        if (testCase.forbidden() && actual != 403) {
            throw new AssertionError("Expected 403 for " + testCase + " but got " + actual);
        }
        if (!testCase.forbidden() && actual == 403) {
            throw new AssertionError("Unexpected 403 for " + testCase);
        }
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor user(String role) {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                .user("matrix-user").roles(role);
    }

    @ParameterizedTest(name = "dashboard as {0}")
    @MethodSource("roles")
    void dashboardAllowsAnyAuthenticatedRole(String role) throws Exception {
        mvc.perform(get("/api/dashboard").with(user(role)))
                .andExpect(status().isOk());
    }

    static Stream<String> roles() {
        return Stream.of("ADMIN", "MANAGER", "USER");
    }
}
