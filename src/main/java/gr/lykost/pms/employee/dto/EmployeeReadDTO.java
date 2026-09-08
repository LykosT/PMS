package gr.lykost.pms.employee.dto;

import gr.lykost.pms.employee.NamedRef;
import gr.lykost.pms.shared.enums.EmployeeStatus;
import gr.lykost.pms.shared.enums.SeniorityLevel;
import gr.lykost.pms.shared.enums.SystemRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private Long departmentId;
    private String departmentName;
//    private Long teamId;
//    private String teamName;
    private Set<NamedRef> businessRoles = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
