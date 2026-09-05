package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import gr.lykost.pms.support.DomainFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeRelationsTest extends AbstractIntegrationTest {

    private MockHttpSession session;

    @BeforeEach
    void signIn() throws Exception {
        session = DomainFixtures.adminSession(mvc);
    }

    @Test
    void createWithBusinessRolesReturnsThem() throws Exception {
        long roleId = DomainFixtures.create(mvc, session, json, "/api/business-roles", """
                {"name":"Relations Engineer","description":"d"}""");

        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Rel","lastName":"Roles",
                                 "email":"relroles@pms.test","phone":"2100001001",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"relroles","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "businessRoleIds":[%d]}""".formatted(roleId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessRoles[0].id").value((int) roleId))
                .andExpect(jsonPath("$.businessRoles[0].name").value("Relations Engineer"));
    }

    @Test
    void createWithUnknownBusinessRoleReturns404() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Bad","lastName":"Role",
                                 "email":"badrole@pms.test","phone":"2100001002",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"badrole","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "businessRoleIds":[99999999]}"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void omittingBusinessRolesYieldsAnEmptyCollection() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"No","lastName":"Roles",
                                 "email":"noroles@pms.test","phone":"2100001003",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"noroles","password":"secret123",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.businessRoles").isArray())
                .andExpect(jsonPath("$.businessRoles").isEmpty());
    }
}
