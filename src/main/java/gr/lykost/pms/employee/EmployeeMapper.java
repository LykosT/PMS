package gr.lykost.pms.employee;

import gr.lykost.pms.employee.dto.EmployeeUserCreateDTO;
import gr.lykost.pms.employee.dto.EmployeeReadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final DepartmentPort departmentPort;
    private final TeamMembershipPort teamMembershipPort;

    public Employee mapToEmployee(EmployeeUserCreateDTO dto) {
        return Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .seniorityLevel(dto.getSeniorityLevel())
                .employeeStatus(dto.getEmployeeStatus())
                .build();
    }

    public EmployeeReadDTO mapToEmployeeReadDTO(Employee employee) {
        User user = employee.getUser();
        return new EmployeeReadDTO(
                employee.getId(),
                employee.getUuid(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getSeniorityLevel(),
                employee.getEmployeeStatus(),
                user != null ? user.getUsername() : null,
                user != null ? user.getSystemRole() : null,
                user != null ? user.isActive() : null,
                employee.getDepartmentId(),
                departmentPort.byId(employee.getDepartmentId()).map(NamedRef::name).orElse(null),
                employee.getBusinessRoles().stream()
                        .map(r -> new NamedRef(r.getId(), r.getName()))
                        .collect(java.util.stream.Collectors.toSet()),
                teamMembershipPort.teamsOf(employee.getId()),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
