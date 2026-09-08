package gr.lykost.pms.businessrole;

/**
 * Lets the businessrole module remove a role from every employee holding it,
 * without importing the employee module (employee already depends on
 * businessrole, so the reverse import would create a cycle).
 */
public interface BusinessRoleAssignmentPort {

    /** Removes the given business role from every employee currently holding it. */
    void unassignFromAllEmployees(Long businessRoleId);
}
