package gr.lykost.pms.authentication;

import gr.lykost.pms.core.enums.SystemRole;
import gr.lykost.pms.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Security principal built from plain values instead of the JPA entity, so it
 * can safely live in the HTTP session without dragging a detached entity
 * (and its lazy associations) along.
 */
public record CustomUserDetails(
        Long id,
        String username,
        String password,
        String fullName,
        SystemRole systemRole,
        boolean active
) implements UserDetails {

    public static CustomUserDetails fromUser(User user) {
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmployee().getFullName(),
                user.getSystemRole(),
                user.isActive()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + systemRole.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
