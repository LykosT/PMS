package gr.lykost.pms.dto.employee;

import gr.lykost.pms.core.enums.SeniorityLevel;
import gr.lykost.pms.model.Department;
import gr.lykost.pms.model.Project;
import gr.lykost.pms.model.Task;
import gr.lykost.pms.model.Team;

import java.util.Set;

public record EmployeeResponseDTO(

        String uuid,
        String firstName,
        String lastName,
        String email,
        String phone ,
        SeniorityLevel seniority,
        String jobTitle,
        boolean active,
        Set<Team> teams,
        Set<Task> tasks,
        Set<Project> projects,
        Department department

) {}
