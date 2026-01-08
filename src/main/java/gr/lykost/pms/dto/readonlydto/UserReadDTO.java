package gr.lykost.pms.dto.readonlydto;

import gr.lykost.pms.core.enums.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReadDTO {
    private Long id;
    private String uuid;
    private String username;
    private SystemRole systemRole;
    private boolean isActive;
    private Long employeeId;
    private String employeeFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}