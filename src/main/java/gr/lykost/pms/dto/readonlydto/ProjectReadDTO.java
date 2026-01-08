package gr.lykost.pms.dto.readonlydto;

import gr.lykost.pms.core.enums.Priority;
import gr.lykost.pms.core.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectReadDTO {
    private Long id;
    private String uuid;
    private String name;
    private String description;
    private ProjectStatus projectStatus;
    private Priority priority;
    private LocalDate startDate;
    private Long departmentId;
    private String departmentName;
    private Long managerId;
    private String managerFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}