package gr.lykost.pms.mapper;

import gr.lykost.pms.dto.createdto.EmployeeUserCreateDTO;
import gr.lykost.pms.dto.readonlydto.EmployeeReadDTO;
import gr.lykost.pms.model.Employee;
import org.springframework.stereotype.Component;
@Component
public class EmployeeMapper {

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
        return new EmployeeReadDTO(
                employee.getUuid(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getSeniorityLevel(),
                employee.getEmployeeStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}