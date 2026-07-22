package com.countyassembly.caims.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * Role Service
 * ============================================================
 * This class contains the business logic for managing roles.
 * Responsibilities:
 * - Create new roles
 * - Retrieve roles
 * - Prevent duplicate role names
 * - Find roles by name
 *
 */
@Service // Registers this class as a Spring Bean.
@RequiredArgsConstructor // Lombok generates a constructor for final fields.
public class RoleService {

     // @RequiredArgsConstructor, Spring injects it automatically.
    private final RoleRepository roleRepository;

    public Role save(Role role) {

        // Check whether a role with the same name already exists.
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException(
                    "Role '" + role.getName() + "' already exists."
            );
        }

        return roleRepository.save(role);
    }

    /**
     * Retrieve all roles.
     *
     * @return List of roles.
     */
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    /**
     * Find a role by its ID.
     *
     * @param id Role ID.
     * @return Optional containing the role if found.
     */
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * Find a role using its name.
     *
     * Example:
     * ADMIN
     * STOREKEEPER
     *
     * @param name Role name.
     * @return Optional Role.
     */
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Delete a role by ID.
     *
     * @param id Role ID.
     */
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}