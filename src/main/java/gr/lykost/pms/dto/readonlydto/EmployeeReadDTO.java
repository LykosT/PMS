package gr.lykost.pms.dto.readonlydto;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeReadDTO {

    private String uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private SeniorityLevel seniorityLevel;
    private EmployeeStatus employeeStatus;
//    private Long departmentId;
//    private String departmentName;
//    private Long teamId;
//    private String teamName;
//    private Set<String> businessRoleNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}