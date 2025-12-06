package gr.lykost.pms.dto.projects;

import gr.lykost.pms.core.enums.Priorities;
import gr.lykost.pms.core.enums.ProjectStatus;
import java.time.LocalDate;
import java.util.List;

public record ProjectResponseDTO(

        String uuid,
        String title,
        String description,
        ProjectStatus status,
        Priorities priority,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate deadline,
        Integer estimatedHours,
        String managerUuid,
        String managerFullName,
        List<String> employeeUuids,
        int totalTasks

) { }
