package com.countyassembly.caims.user;

import com.countyassembly.caims.common.entity.DuplicateResourceException;
import com.countyassembly.caims.role.Role;
import com.countyassembly.caims.role.RoleService;
import com.countyassembly.caims.security.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemUserServiceImpl implements SystemUserService {

    private final SystemUserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Override
    public SystemUser createUser(SystemUser user, Long roleId, String rawPassword) {

        String username = user.getUsername().trim();
        String email = user.getEmail().trim();

        // Validate username uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username '" + username + "' is already taken.");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email '" + email + "' is already in use.");
        }

        // Validate password (NEW!)
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required for a new user.");
        }

        List<String> passwordErrors = passwordValidator.validate(rawPassword);
        if (!passwordErrors.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password policy violated: " + String.join(" ", passwordErrors)
            );
        }

        Optional<Role> roleOptional = resolveRole(roleId);
        if (roleOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid role selected.");
        }
        Role role = roleOptional.get();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        if (user.getActive() == null) {
            user.setActive(true);
        }

        return userRepository.save(user);
    }

    @Override
    public SystemUser updateUser(Long id, SystemUser incoming, Long roleId, String rawPassword) {

        SystemUser existing = findById(id);

        String username = incoming.getUsername().trim();
        String email = incoming.getEmail().trim();

        // Validate username uniqueness (if changed)
        if (!existing.getUsername().equalsIgnoreCase(username)
                && userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username '" + username + "' is already taken.");
        }

        // Validate email uniqueness (if changed)
        if (!existing.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email '" + email + "' is already in use.");
        }

        Optional<Role> roleOptional = resolveRole(roleId);
        if (roleOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid role selected.");
        }
        Role role = roleOptional.get();

        existing.setFirstName(incoming.getFirstName());
        existing.setLastName(incoming.getLastName());
        existing.setUsername(username);
        existing.setEmail(email);
        existing.setPhoneNumber(incoming.getPhoneNumber());
        existing.setRole(role);

        // Only validate and update password if a new one was provided
        if (rawPassword != null && !rawPassword.isBlank()) {
            List<String> passwordErrors = passwordValidator.validate(rawPassword);
            if (!passwordErrors.isEmpty()) {
                throw new IllegalArgumentException(
                        "Password policy violated: " + String.join(" ", passwordErrors)
                );
            }
            existing.setPassword(passwordEncoder.encode(rawPassword));
        }

        return userRepository.save(existing);
    }

    private Optional<Role> resolveRole(Long roleId) {
        return roleService.findById(roleId);
    }

    @Override
    public SystemUser setActive(Long id, boolean active) {
        return null;
    }

    @Override
    public SystemUser findById(Long id) {
        return null;
    }

    @Override
    public SystemUser findByUsername(String username) {
        return null;
    }

    @Override
    public List<SystemUser> findAll() {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    // ... rest of your existing methods remain unchanged ...
}