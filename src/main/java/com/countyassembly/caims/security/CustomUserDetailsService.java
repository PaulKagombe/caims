package com.countyassembly.caims.security;

import com.countyassembly.caims.user.SystemUser;
import com.countyassembly.caims.user.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ============================================================
 * CustomUserDetailsService
 * ============================================================
 *
 * Loads users from the CAIMS database during authentication.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository used to retrieve users.
     */
    private final SystemUserRepository userRepository;

    /**
     * Finds a user by username.
     *
     * This method is automatically called by Spring Security
     * whenever someone attempts to log in.
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        log.debug("Attempting to load user with username: {}", username);

        SystemUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException(
                            "User not found: " + username);
                });

        log.debug("User {} loaded successfully with role: {}", 
                username, 
                user.getRole() != null ? user.getRole().getName() : "NONE");

        return new CustomUserDetails(user);
    }
}