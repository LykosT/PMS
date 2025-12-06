package gr.lykost.pms.dto.departments;

import gr.lykost.pms.model.Employee;
import java.util.Set;

public record DepartmentResponseDTO(

        String uuid,
        String title,
        String description,
        Set<Employee> employees

) {}
