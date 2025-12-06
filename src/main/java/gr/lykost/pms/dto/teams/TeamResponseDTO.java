package gr.lykost.pms.dto.teams;

import gr.lykost.pms.model.Employee;

import java.util.Set;

public record TeamResponseDTO(

        String uuid,
        String title,
        String description,
        Set<Employee> members,
        Employee teamLead

) {}
