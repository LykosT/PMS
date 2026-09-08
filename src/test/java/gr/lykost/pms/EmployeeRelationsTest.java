package gr.lykost.pms;

import gr.lykost.pms.support.AbstractIntegrationTest;
import gr.lykost.pms.support.DomainFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void editOmittingBusinessRoleIdsPreservesExistingRoles() throws Exception {
        long roleId = DomainFixtures.create(mvc, session, json, "/api/business-roles", """
                {"name":"Preserve Role","description":"d"}""");

        String createResponse = mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Keep","lastName":"Roles",
                                 "email":"keeproles@pms.test","phone":"2100001004",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"keeproles","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "businessRoleIds":[%d]}""".formatted(roleId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long employeeId = json.readTree(createResponse).get("id").asLong();

        mvc.perform(put("/api/employees/{id}", employeeId).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Keep","lastName":"Roles",
                                 "email":"keeproles@pms.test","phone":"2100001004",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"keeproles",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessRoles[0].id").value((int) roleId))
                .andExpect(jsonPath("$.businessRoles[0].name").value("Preserve Role"));
    }

    @Test
    void editWithEmptyBusinessRoleIdsClearsExistingRoles() throws Exception {
        long roleId = DomainFixtures.create(mvc, session, json, "/api/business-roles", """
                {"name":"Clear Role","description":"d"}""");

        String createResponse = mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Clear","lastName":"Roles",
                                 "email":"clearroles@pms.test","phone":"2100001005",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"clearroles","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "businessRoleIds":[%d]}""".formatted(roleId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long employeeId = json.readTree(createResponse).get("id").asLong();

        mvc.perform(put("/api/employees/{id}", employeeId).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Clear","lastName":"Roles",
                                 "email":"clearroles@pms.test","phone":"2100001005",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"clearroles",
                                 "systemRole":"USER","active":true,
                                 "businessRoleIds":[]}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessRoles").isArray())
                .andExpect(jsonPath("$.businessRoles").isEmpty());
    }

    @Test
    void createWithDepartmentReturnsIdAndName() throws Exception {
        var ids = DomainFixtures.seed(mvc, session, json);

        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Dep","lastName":"Member",
                                 "email":"depmember@pms.test","phone":"2100001004",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"depmember","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "departmentId":%d}""".formatted(ids.departmentId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentId").value((int) ids.departmentId()))
                .andExpect(jsonPath("$.departmentName").isNotEmpty());
    }

    @Test
    void createWithoutDepartmentLeavesItNull() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"No","lastName":"Department",
                                 "email":"nodept@pms.test","phone":"2100001005",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"nodept","password":"secret123",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentId").doesNotExist())
                .andExpect(jsonPath("$.departmentName").doesNotExist());
    }

    @Test
    void createWithUnknownDepartmentReturns404() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Bad","lastName":"Department",
                                 "email":"baddept@pms.test","phone":"2100001006",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"baddept","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "departmentId":99999999}"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithTeamsReturnsThem() throws Exception {
        var ids = DomainFixtures.seed(mvc, session, json);

        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Team","lastName":"Member",
                                 "email":"teammember@pms.test","phone":"2100001007",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"teammember","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "teamIds":[%d]}""".formatted(ids.teamId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teams[0].id").value((int) ids.teamId()));
    }

    @Test
    void employeeCanBelongToTwoTeams() throws Exception {
        var a = DomainFixtures.seed(mvc, session, json);
        var b = DomainFixtures.seed(mvc, session, json);

        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Multi","lastName":"Team",
                                 "email":"multiteam@pms.test","phone":"2100001008",
                                 "seniorityLevel":"SENIOR","employeeStatus":"ACTIVE",
                                 "username":"multiteam","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "teamIds":[%d,%d]}""".formatted(a.teamId(), b.teamId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teams.length()").value(2));
    }

    @Test
    void editReplacesTeamMembership() throws Exception {
        var a = DomainFixtures.seed(mvc, session, json);
        var b = DomainFixtures.seed(mvc, session, json);

        long id = DomainFixtures.create(mvc, session, json, "/api/employees", """
                {"firstName":"Move","lastName":"Teams",
                 "email":"moveteams@pms.test","phone":"2100001009",
                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                 "username":"moveteams","password":"secret123",
                 "systemRole":"USER","active":true,
                 "teamIds":[%d]}""".formatted(a.teamId()));

        mvc.perform(put("/api/employees/" + id).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Move","lastName":"Teams",
                                 "email":"moveteams@pms.test","phone":"2100001009",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"moveteams",
                                 "systemRole":"USER","active":true,
                                 "teamIds":[%d]}""".formatted(b.teamId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams.length()").value(1))
                .andExpect(jsonPath("$.teams[0].id").value((int) b.teamId()));
    }

    @Test
    void createWithUnknownTeamReturns404() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Bad","lastName":"Team",
                                 "email":"badteam@pms.test","phone":"2100001010",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"badteam","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "teamIds":[99999999]}"""))
                .andExpect(status().isNotFound());
    }

    @Test
    void editOmittingTeamIdsPreservesExistingTeams() throws Exception {
        var a = DomainFixtures.seed(mvc, session, json);

        String createResponse = mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Keep","lastName":"Teams",
                                 "email":"keepteams@pms.test","phone":"2100001011",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"keepteams","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "teamIds":[%d]}""".formatted(a.teamId())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long employeeId = json.readTree(createResponse).get("id").asLong();

        mvc.perform(put("/api/employees/{id}", employeeId).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Keep","lastName":"Teams",
                                 "email":"keepteams@pms.test","phone":"2100001011",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"keepteams",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams.length()").value(1))
                .andExpect(jsonPath("$.teams[0].id").value((int) a.teamId()));
    }

    @Test
    void editOmittingDepartmentIdClearsDepartment() throws Exception {
        var ids = DomainFixtures.seed(mvc, session, json);

        String createResponse = mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Has","lastName":"Department",
                                 "email":"hasdept@pms.test","phone":"2100001012",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"hasdept","password":"secret123",
                                 "systemRole":"USER","active":true,
                                 "departmentId":%d}""".formatted(ids.departmentId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentId").value((int) ids.departmentId()))
                .andReturn().getResponse().getContentAsString();
        long employeeId = json.readTree(createResponse).get("id").asLong();

        // departmentId omitted on edit -> a scalar field, so this unconditionally
        // clears it (unlike businessRoleIds/teamIds, where omission means "leave
        // unchanged"). This is deliberate (a plain Long can't tell omitted from null).
        mvc.perform(put("/api/employees/{id}", employeeId).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Has","lastName":"Department",
                                 "email":"hasdept@pms.test","phone":"2100001012",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"hasdept",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").doesNotExist())
                .andExpect(jsonPath("$.departmentName").doesNotExist());
    }

    @Test
    void editWithEmptyTeamIdsClearsExistingTeams() throws Exception {
        var a = DomainFixtures.seed(mvc, session, json);

        long employeeId = DomainFixtures.create(mvc, session, json, "/api/employees", """
                {"firstName":"Clear","lastName":"Teams",
                 "email":"clearteams@pms.test","phone":"2100001013",
                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                 "username":"clearteams","password":"secret123",
                 "systemRole":"USER","active":true,
                 "teamIds":[%d]}""".formatted(a.teamId()));

        mvc.perform(put("/api/employees/{id}", employeeId).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Clear","lastName":"Teams",
                                 "email":"clearteams@pms.test","phone":"2100001013",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"clearteams",
                                 "systemRole":"USER","active":true,
                                 "teamIds":[]}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams").isArray())
                .andExpect(jsonPath("$.teams").isEmpty());
    }

    @Test
    void deletingEmployeeWithTeamMembershipsSucceeds() throws Exception {
        // Regression test for C1: deleting an employee who belongs to a team
        // used to fail with 409 because nothing cleared the employee_team
        // join rows (Team owns that join table, not Employee) before the
        // employee row was deleted.
        var a = DomainFixtures.seed(mvc, session, json);
        var b = DomainFixtures.seed(mvc, session, json);

        long employeeId = DomainFixtures.create(mvc, session, json, "/api/employees", """
                {"firstName":"OnTeams","lastName":"ToDelete",
                 "email":"onteamstodelete@pms.test","phone":"2100001014",
                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                 "username":"onteamstodelete","password":"secret123",
                 "systemRole":"USER","active":true,
                 "teamIds":[%d,%d]}""".formatted(a.teamId(), b.teamId()));

        mvc.perform(delete("/api/employees/{id}", employeeId).with(csrf()).session(session))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/employees/{id}", employeeId).session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingBusinessRoleAssignedToEmployeeSucceeds() throws Exception {
        // Regression test for C2: deleting a business role held by an
        // employee used to fail with 409 because nothing cleared the
        // employee_businessrole join rows (Employee owns that join table)
        // before the role row was deleted.
        long roleId = DomainFixtures.create(mvc, session, json, "/api/business-roles", """
                {"name":"Deletable Assigned Role","description":"d"}""");

        DomainFixtures.create(mvc, session, json, "/api/employees", """
                {"firstName":"Assigned","lastName":"RoleHolder",
                 "email":"roleholder@pms.test","phone":"2100001015",
                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                 "username":"roleholder","password":"secret123",
                 "systemRole":"USER","active":true,
                 "businessRoleIds":[%d]}""".formatted(roleId));

        mvc.perform(delete("/api/business-roles/{id}", roleId).with(csrf()).session(session))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/business-roles/{id}", roleId).session(session))
                .andExpect(status().isNotFound());
    }
}
