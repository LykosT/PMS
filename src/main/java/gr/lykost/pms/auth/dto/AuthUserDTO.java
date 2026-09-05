package gr.lykost.pms.auth.dto;

import gr.lykost.pms.shared.enums.SystemRole;

/**
 * The authenticated user's identity, returned by the auth endpoints.
 */
public record AuthUserDTO(
        String username,
        String fullName,
        SystemRole systemRole
) {
}
