package gr.lykost.pms.dto.readonlydto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamReadDTO {
    private Long id;
    private String uuid;
    private String name;
    private String description;
    private Long departmentId;
    private String departmentName;
    private Long projectId;
    private String projectName;
    private Long leadId;
    private String leadFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}