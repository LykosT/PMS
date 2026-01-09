package gr.lykost.pms.dto.createdto;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class EmployeeCreateDTO {
//
//    @NotBlank(message = "First name is required")
//    private String firstName;
//
//    @NotBlank(message = "Last name is required")
//    private String lastName;
//
//    @NotBlank(message = "Email is required")
//    @Email(message = "Invalid email format")
//    private String email;
//
//    private String phone;
//
//    //@NotNull(message = "Seniority level is required")
//    private SeniorityLevel seniorityLevel;
//
//    //@NotNull(message = "Employee status is required")
//    private EmployeeStatus employeeStatus;
//
//    @Valid
//    private UserCreateDTO userCreateDTO;
//
//    //@NotNull(message = "Department ID is required")
//    private Long departmentId;
//
//    //@NotNull(message = "Team ID is required")
//    private Long teamId;
//
//    // Optional list of role IDs to assign immediately
//    private Set<Long> businessRoleIds;
//}