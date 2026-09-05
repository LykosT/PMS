package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import gr.lykost.pms.support.DomainFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleAccessTest extends AbstractIntegrationTest {

    private static final List<String> RESOURCES = List.of(
            "employees", "departments", "projects", "teams", "tasks", "business-roles");

    // Valid POST/PUT payload templates so a syntactically/semantically valid body reaches
    // argument resolution and the request actually falls through to the @PreAuthorize
    // check under test, instead of failing bean validation before authorization is ever
    // evaluated (see task-5-report.md for why "{}" bodies could not exercise this boundary).
    // The five non-employee templates are lifted from ResourceCrudTest's known-valid
    // payloads. %N% is a per-call uniqueness counter, %EMP%/%DEPT%/%PROJ%/%TEAM% are ids
    // from this test's own DomainFixtures seed.
    private static final Map<String, String> CREATE_TEMPLATES = Map.of(
            "employees", """
                    {"firstName":"Matrix","lastName":"Employee","email":"matrix-%N%@pms.test",
                     "phone":"2100000000","seniorityLevel":"JUNIOR","employeeStatus":"ACTIVE",
                     "username":"matrix-%N%","password":"secret123","systemRole":"USER","active":true}""",
            "business-roles", """
                    {"name":"Matrix Role %N%","description":"before"}""",
            "departments", """
                    {"name":"Matrix Dept","description":"before","managerId":%EMP%}""",
            "projects", """
                    {"name":"Matrix Project","description":"before","projectStatus":"PLANNED",
                     "priority":"LOW","startDate":"2026-02-01",
                     "departmentId":%DEPT%,"managerId":%EMP%}""",
            "teams", """
                    {"name":"Matrix Team","description":"before",
                     "departmentId":%DEPT%,"projectId":%PROJ%,"leadId":%EMP%}""",
            "tasks", """
                    {"name":"Matrix Task","description":"before","taskStatus":"TODO",
                     "priority":"MEDIUM","dueDate":"2026-03-01",
                     "projectId":%PROJ%,"teamId":%TEAM%,"assigneeId":%EMP%}"""
    );

    private static final Map<String, String> UPDATE_TEMPLATES = Map.of(
            // employees/1 is handled separately in bodyFor(): id 1 is the seeded master
            // admin (MasterUserInitializer) shared by every test class in the JVM-lifetime
            // container, so its update body must be idempotent rather than templated.
            "employees", "",
            "business-roles", """
                    {"name":"Matrix Role Updated %N%","description":"after"}""",
            "departments", """
                    {"name":"Matrix Dept Updated","description":"after","managerId":%EMP%}""",
            "projects", """
                    {"name":"Matrix Project Updated","description":"after","projectStatus":"PLANNED",
                     "priority":"LOW","startDate":"2026-02-01",
                     "departmentId":%DEPT%,"managerId":%EMP%}""",
            "teams", """
                    {"name":"Matrix Team Updated","description":"after",
                     "departmentId":%DEPT%,"projectId":%PROJ%,"leadId":%EMP%}""",
            "tasks", """
                    {"name":"Matrix Task Updated","description":"after","taskStatus":"TODO",
                     "priority":"MEDIUM","dueDate":"2026-03-01",
                     "projectId":%PROJ%,"teamId":%TEAM%,"assigneeId":%EMP%}"""
    );

    private static final String EMPLOYEE_1_IDEMPOTENT_UPDATE = """
            {"firstName":"System","lastName":"Administrator","email":"admin@admin.admin",
             "seniorityLevel":"MANAGER","employeeStatus":"ACTIVE",
             "username":"admin","systemRole":"ADMIN","active":true}""";

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    // Static + guarded rather than a fresh login/seed per case: the matrix itself sends a
    // real DELETE /api/employees/1 as ADMIN, which genuinely deletes the seeded master
    // admin row (id 1, see MasterUserInitializer) once that case runs. A per-case
    // re-login as "admin"/"admin" would then fail for every case after it. Logging in
    // once and reusing the MockHttpSession works because HttpSessionSecurityContextRepository
    // caches the resolved Authentication in the session and does not re-verify against the
    // database on every request, so the cached session stays usable even after the
    // backing row is gone. Ids are seeded once too and reused as FK references in every
    // POST/PUT body; that is safe under the "403 vs not-403" assertion even if a later
    // matrix DELETE case removes the referenced row, since an allowed role may legitimately
    // see 400/404 downstream of authorization.
    private static MockHttpSession adminSession;
    private static DomainFixtures.Ids ids;

    @BeforeEach
    void seedFixturesOnce() throws Exception {
        if (adminSession == null) {
            adminSession = DomainFixtures.adminSession(mvc);
            ids = DomainFixtures.seed(mvc, adminSession, json);
        }
    }

    private static String resourceOf(String path) {
        int slash = path.indexOf('/');
        return slash < 0 ? path : path.substring(0, slash);
    }

    /** Builds a valid request body for the given resource/method so the request reaches
     *  the @PreAuthorize check instead of failing validation beforehand. */
    private String bodyFor(String resource, String method) {
        if ("employees".equals(resource) && "PUT".equals(method)) {
            return EMPLOYEE_1_IDEMPOTENT_UPDATE;
        }
        String template = ("POST".equals(method) ? CREATE_TEMPLATES : UPDATE_TEMPLATES).get(resource);
        return template
                .replace("%N%", String.valueOf(SEQ.getAndIncrement()))
                .replace("%EMP%", String.valueOf(ids.employeeId()))
                .replace("%DEPT%", String.valueOf(ids.departmentId()))
                .replace("%PROJ%", String.valueOf(ids.projectId()))
                .replace("%TEAM%", String.valueOf(ids.teamId()));
    }

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
        String resource = resourceOf(testCase.path());
        RequestBuilder request = switch (testCase.method()) {
            case "GET" -> get("/api/" + testCase.path())
                    .with(user(testCase.role()));
            case "POST" -> post("/api/" + testCase.path()).with(csrf())
                    .with(user(testCase.role())).contentType(APPLICATION_JSON)
                    .content(bodyFor(resource, "POST"));
            case "PUT" -> put("/api/" + testCase.path()).with(csrf())
                    .with(user(testCase.role())).contentType(APPLICATION_JSON)
                    .content(bodyFor(resource, "PUT"));
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
