package gr.lykost.pms.support;

import tools.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class DomainFixtures {

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    public record Ids(long employeeId, long departmentId, long projectId, long teamId) {}

    private DomainFixtures() {}

    /** Creates one of each entity in dependency order and returns their ids. */
    public static Ids seed(MockMvc mvc, MockHttpSession session, ObjectMapper json) throws Exception {
        int n = SEQ.getAndIncrement();

        long employeeId = create(mvc, session, json, "/api/employees", """
                {"firstName":"Fixture","lastName":"Person%d",
                 "email":"fixture%d@pms.test","phone":"2100000%03d",
                 "seniorityLevel":"SENIOR","employeeStatus":"ACTIVE",
                 "username":"fixture%d","password":"secret123",
                 "systemRole":"MANAGER","active":true}""".formatted(n, n, n, n));

        long departmentId = create(mvc, session, json, "/api/departments", """
                {"name":"Fixture Dept %d","description":"d","managerId":%d}"""
                .formatted(n, employeeId));

        long projectId = create(mvc, session, json, "/api/projects", """
                {"name":"Fixture Project %d","description":"d",
                 "projectStatus":"IN_PROGRESS","priority":"HIGH","startDate":"2026-01-12",
                 "departmentId":%d,"managerId":%d}""".formatted(n, departmentId, employeeId));

        long teamId = create(mvc, session, json, "/api/teams", """
                {"name":"Fixture Team %d","description":"d",
                 "departmentId":%d,"projectId":%d,"leadId":%d}"""
                .formatted(n, departmentId, projectId, employeeId));

        return new Ids(employeeId, departmentId, projectId, teamId);
    }

    /** POSTs a body and returns the created row's id. */
    public static long create(MockMvc mvc, MockHttpSession session, ObjectMapper json,
                              String path, String body) throws Exception {
        String response = mvc.perform(post(path).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("id").asLong();
    }

    /** Logs in as the seeded admin and returns the authenticated session. */
    public static MockHttpSession adminSession(MockMvc mvc) throws Exception {
        var result = mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin"}"""))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
