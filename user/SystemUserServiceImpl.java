package com.countyassembly.caims.user;

import com.countyassembly.caims.common.exception.DuplicateResourceException;
import com.countyassembly.caims.common.exception.ResourceNotFoundException;
import com.countyassembly.caims.role.Role;
import com.countyassembly.caims.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * SystemUser Service Implementation
 * ============================================================
 *
 * Implements all business logic for admin-managed user accounts.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SystemUserServiceImpl implements SystemUserService {

    private final SystemUserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SystemUser createUser(SystemUser user, Long roleId, String rawPassword) {

        String username = user.getUsername().trim();
        String email = user.getEmail().trim();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username '" + username + "' is already taken.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email '" + email + "' is already in use.");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required for a new user.");
        }

        Role role = resolveRole(roleId);

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

        if (!existing.getUsername().equalsIgnoreCase(username)
                && userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException(
                    "Username '" + username + "' is already taken.");
        }

        if (!existing.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email '" + email + "' is already in use.");
        }

        Role role = resolveRole(roleId);

        existing.setFirstName(incoming.getFirstName());
        existing.setLastName(incoming.getLastName());
        existing.setUsername(username);
        existing.setEmail(email);
        existing.setPhoneNumber(incoming.getPhoneNumber());
        existing.setRole(role);

        // Blank password on the edit form means "leave it unchanged".
        if (rawPassword != null && !rawPassword.isBlank()) {
            existing.setPassword(passwordEncoder.encode(rawPassword));
        }

        return userRepository.save(existing);
    }

    @Override
    public SystemUser setActive(Long id, boolean active) {

        SystemUser user = findById(id);

        user.setActive(active);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemUser findById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SystemUser findByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemUser> findAll() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Role resolveRole(Long roleId) {

        if (roleId == null) {
            throw new IllegalArgumentException("A role must be selected.");
        }

        return roleService.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found with id: " + roleId));
    }
}
