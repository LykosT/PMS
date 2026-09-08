package gr.lykost.pms.employee;

import java.util.Optional;

/**
 * Lets the employee module read department names without importing the
 * department module, which would create a dependency cycle.
 */
public interface DepartmentPort {

    Optional<NamedRef> byId(Long departmentId);
}
