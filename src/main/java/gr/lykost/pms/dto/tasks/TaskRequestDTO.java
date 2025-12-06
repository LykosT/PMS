package gr.lykost.pms.dto.tasks;

import gr.lykost.pms.core.enums.Priorities;
import gr.lykost.pms.model.Employee;
import gr.lykost.pms.model.Project;

import java.time.LocalDate;

public record TaskRequestDTO(

        String title,
        String description,
        Priorities priority,
        Integer estimatedHours,
        LocalDate dueDate,
        Employee assignee,
        Project project

) {}