package gr.lykost.pms.dto.employee;

import gr.lykost.pms.core.enums.SeniorityLevel;

public record EmployeeRequestDTO(

        String firstName,
        String lastName,
        String email,
        String phone ,
        SeniorityLevel seniority,
        String jobTitle,
        boolean active

) {}
