package gr.lykost.pms.dto.user;

import gr.lykost.pms.core.enums.UserRole;
import gr.lykost.pms.model.Employee;

public record UserResponseDTO(

        String username,
        String password,
        UserRole role,
        boolean active,
        Employee employeeProfile

) {}
