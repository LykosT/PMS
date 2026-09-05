package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import gr.lykost.pms.support.DomainFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpSession;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResourceCrudTest extends AbstractIntegrationTest {

    record ResourceCase(String path, String createTemplate, String updateTemplate) {
        @Override public String toString() { return path; }
    }

    private MockHttpSession session;
    private DomainFixtures.Ids ids;

    @BeforeEach
    void signIn() throws Exception {
        session = DomainFixtures.adminSession(mvc);
        ids = DomainFixtures.seed(mvc, session, json);
    }

    static Stream<ResourceCase> resources() {
        return Stream.of(
                new ResourceCase("/api/business-roles",
                        """
                        {"name":"Crud Role","description":"before"}""",
                        """
                        {"name":"Crud Role","description":"after"}"""),
                new ResourceCase("/api/departments",
                        """
                        {"name":"Crud Dept","description":"before","managerId":%EMP%}""",
                        """
                        {"name":"Crud Dept","description":"after","managerId":%EMP%}"""),
                new ResourceCase("/api/projects",
                        """
                        {"name":"Crud Project","description":"before","projectStatus":"PLANNED",
                         "priority":"LOW","startDate":"2026-02-01",
                         "departmentId":%DEPT%,"managerId":%EMP%}""",
                        """
                        {"name":"Crud Project","description":"after","projectStatus":"PLANNED",
                         "priority":"LOW","startDate":"2026-02-01",
                         "departmentId":%DEPT%,"managerId":%EMP%}"""),
                new ResourceCase("/api/teams",
                        """
                        {"name":"Crud Team","description":"before",
                         "departmentId":%DEPT%,"projectId":%PROJ%,"leadId":%EMP%}""",
                        """
                        {"name":"Crud Team","description":"after",
                         "departmentId":%DEPT%,"projectId":%PROJ%,"leadId":%EMP%}"""),
                new ResourceCase("/api/tasks",
                        """
                        {"name":"Crud Task","description":"before","taskStatus":"TODO",
                         "priority":"MEDIUM","dueDate":"2026-03-01",
                         "projectId":%PROJ%,"teamId":%TEAM%,"assigneeId":%EMP%}""",
                        """
                        {"name":"Crud Task","description":"after","taskStatus":"TODO",
                         "priority":"MEDIUM","dueDate":"2026-03-01",
                         "projectId":%PROJ%,"teamId":%TEAM%,"assigneeId":%EMP%}""")
        );
    }

    private String resolve(String template) {
        return template
                .replace("%EMP%", String.valueOf(ids.employeeId()))
                .replace("%DEPT%", String.valueOf(ids.departmentId()))
                .replace("%PROJ%", String.valueOf(ids.projectId()))
                .replace("%TEAM%", String.valueOf(ids.teamId()));
    }

    @ParameterizedTest(name = "CRUD round trip for {0}")
    @MethodSource("resources")
    void crudRoundTrip(ResourceCase resource) throws Exception {
        // CREATE -> 201
        String created = mvc.perform(post(resource.path()).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content(resolve(resource.createTemplate())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        long id = json.readTree(created).get("id").asLong();

        // READ -> 200
        mvc.perform(get(resource.path() + "/" + id).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("before"));

        // LIST -> 200 and contains the row just created
        mvc.perform(get(resource.path()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        // UPDATE -> 200 with the changed field
        mvc.perform(put(resource.path() + "/" + id).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content(resolve(resource.updateTemplate())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("after"));

        // DELETE -> 204, then READ -> 404
        mvc.perform(delete(resource.path() + "/" + id).with(csrf()).session(session))
                .andExpect(status().isNoContent());

        mvc.perform(get(resource.path() + "/" + id).session(session))
                .andExpect(status().isNotFound());
    }
}
