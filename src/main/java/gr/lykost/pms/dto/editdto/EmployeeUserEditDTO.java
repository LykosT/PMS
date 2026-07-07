package gr.lykost.pms.dto.editdto;

import gr.lykost.pms.core.enums.EmployeeStatus;
import gr.lykost.pms.core.enums.SeniorityLevel;
import gr.lykost.pms.core.enums.SystemRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
public class EmployeeUserEditDTO {

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

    // Optional on edit: leave null/empty to keep the current password
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "System Role is required")
    private SystemRole systemRole;

    private boolean active = true;
}
