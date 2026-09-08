package gr.lykost.pms.employee;

import java.util.Set;

/**
 * Lets the employee module read and set team membership without importing
 * the team module, which would create a dependency cycle.
 */
public interface TeamMembershipPort {

    Set<NamedRef> teamsOf(Long employeeId);

    /** Replaces the employee's membership with exactly {@code teamIds}. */
    void assign(Long employeeId, Set<Long> teamIds);
}
