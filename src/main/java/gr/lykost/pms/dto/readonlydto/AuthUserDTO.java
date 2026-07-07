package gr.lykost.pms.dto.readonlydto;

import gr.lykost.pms.core.enums.SystemRole;

/**
 * The authenticated user's identity, returned by the auth endpoints.
 */
public record AuthUserDTO(
        String username,
        String fullName,
        SystemRole systemRole
) {
}
