package com.countyassembly.caims.security;

import com.countyassembly.caims.user.SystemUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * ============================================================
 * CustomUserDetails
 * ============================================================
 *
 * Adapts the SystemUser entity to Spring Security's UserDetails.
 * This allows Spring Security to authenticate users stored
 * in the CAIMS database.
 */
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetails implements UserDetails {

    /**
     * The authenticated user from the database.
     */
    private final SystemUser user;

    /**
     * Returns the user's role(s).
     *
     * Spring Security expects roles to begin with "ROLE_".
     *
     * Example:
     * ADMIN  -> ROLE_ADMIN
     * MEMBER -> ROLE_MEMBER
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (user.getRole() == null) {
            log.warn("User {} has no assigned role", user.getUsername());
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );
    }

    /**
     * Returns the encrypted password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used during login.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Whether the account has expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Whether the account is locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Whether the password has expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Whether the account is enabled.
     */
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getActive());
    }

    /**
     * Returns the original SystemUser object.
     * Useful throughout the application after login.
     */
    public SystemUser getUser() {
        return user;
    }
}