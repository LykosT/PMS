package gr.lykost.pms.dto.readonlydto;

import gr.lykost.pms.core.enums.Priority;
import gr.lykost.pms.core.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReadDTO {
    private Long id;
    private String uuid;
    private String name;
    private String description;
    private TaskStatus taskStatus;
    private Priority priority;
    private LocalDate dueDate;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}