package gr.lykost.pms.dto.createdto;

import gr.lykost.pms.core.enums.Priority;
import gr.lykost.pms.core.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDTO {

    @NotBlank(message = "Task name is required")
    private String name;

    private String description;

    @NotNull(message = "Task status is required")
    private TaskStatus taskStatus;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private LocalDate dueDate;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Team ID is required")
    private Long teamId;

    // Optional: A task might not be assigned immediately
    private Long assigneeId;
}