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

class EmployeeApiTest extends AbstractIntegrationTest {

    private MockHttpSession session;

    @BeforeEach
    void signIn() throws Exception {
        session = DomainFixtures.adminSession(mvc);
    }

    @Test
    void createReturnsEmployeeWithItsLinkedAccount() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Ada","lastName":"Lovelace",
                                 "email":"ada@pms.test","phone":"2100000001",
                                 "seniorityLevel":"LEAD","employeeStatus":"ACTIVE",
                                 "username":"ada","password":"secret123",
                                 "systemRole":"MANAGER","active":true}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.username").value("ada"))
                .andExpect(jsonPath("$.systemRole").value("MANAGER"))
                .andExpect(jsonPath("$.userActive").value(true));
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        String body = """
                {"firstName":"Dup","lastName":"Licate",
                 "email":"dup@pms.test","phone":"2100000002",
                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                 "username":"dup1","password":"secret123",
                 "systemRole":"USER","active":true}""";

        mvc.perform(post("/api/employees").with(csrf()).session(session)
                .contentType(APPLICATION_JSON).content(body)).andExpect(status().isCreated());

        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content(body.replace("dup1", "dup2")))
                .andExpect(status().isConflict());
    }

    @Test
    void blankPasswordOnEditKeepsTheExistingPassword() throws Exception {
        long id = DomainFixtures.create(mvc, session, json, "/api/employees", """
                {"firstName":"Keep","lastName":"Password",
                 "email":"keep@pms.test","phone":"2100000003",
                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                 "username":"keeper","password":"secret123",
                 "systemRole":"USER","active":true}""");

        mvc.perform(put("/api/employees/" + id).with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Keep","lastName":"Renamed",
                                 "email":"keep@pms.test","phone":"2100000003",
                                 "seniorityLevel":"SENIOR","employeeStatus":"ACTIVE",
                                 "username":"keeper",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Renamed"));

        // The original password must still authenticate.
        mvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(APPLICATION_JSON).content("""
                                {"username":"keeper","password":"secret123"}"""))
                .andExpect(status().isOk());
    }

    @Test
    void invalidPhoneReturns400WithFieldError() throws Exception {
        mvc.perform(post("/api/employees").with(csrf()).session(session)
                        .contentType(APPLICATION_JSON).content("""
                                {"firstName":"Bad","lastName":"Phone",
                                 "email":"badphone@pms.test","phone":"+30 210 555 0101",
                                 "seniorityLevel":"MID","employeeStatus":"ACTIVE",
                                 "username":"badphone","password":"secret123",
                                 "systemRole":"USER","active":true}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.phone").isNotEmpty());
    }
}
