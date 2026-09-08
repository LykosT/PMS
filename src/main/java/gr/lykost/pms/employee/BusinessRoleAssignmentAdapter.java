package gr.lykost.pms.employee;

import gr.lykost.pms.businessrole.BusinessRoleAssignmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
class BusinessRoleAssignmentAdapter implements BusinessRoleAssignmentPort {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void unassignFromAllEmployees(Long businessRoleId) {
        List<Employee> holders = employeeRepository.findByBusinessRoles_Id(businessRoleId);
        for (Employee employee : holders) {
            employee.getBusinessRoles().removeIf(role -> role.getId().equals(businessRoleId));
        }
        employeeRepository.saveAll(holders);
    }
}
