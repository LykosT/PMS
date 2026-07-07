package gr.lykost.pms.dto.readonlydto;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import gr.lykost.pms.core.enums.SystemRole;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeReadDTO {

    private Long id;
    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private SeniorityLevel seniorityLevel;
    private EmployeeStatus employeeStatus;

    // Linked user account (null when the employee has no account)
    private String username;
    private SystemRole systemRole;
    private Boolean userActive;

//    private Long departmentId;
//    private String departmentName;
//    private Long teamId;
//    private String teamName;
//    private Set<String> businessRoleNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
