package gr.lykost.pms.employee.dto;

import gr.lykost.pms.shared.enums.EmployeeStatus;
import gr.lykost.pms.shared.enums.SeniorityLevel;
import gr.lykost.pms.shared.enums.SystemRole;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeUserCreateDTO {

    // Employee Information
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @NotNull(message = "Seniority level is required")
    private SeniorityLevel seniorityLevel;

    @NotNull(message = "Employee status is required")
    private EmployeeStatus employeeStatus;

    // User Information
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "System Role is required")
    private SystemRole systemRole;

    private boolean active = true;

    // null and empty behave identically on create (both yield no roles); kept null for
    // symmetry with EmployeeUserEditDTO, where null vs. empty is meaningful.
    private Set<Long> businessRoleIds = null;
}