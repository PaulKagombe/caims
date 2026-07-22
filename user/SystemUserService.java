package com.countyassembly.caims.user;

import java.util.List;

/**
 * ============================================================
 * SystemUser Service
 * ============================================================
 *
 * Defines the business operations for managing users, available
 * to System Administrators for role-based user management.
 */
public interface SystemUserService {

    /**
     * Create a new user.
     *
     * @param user        Populated user (password field must hold the
     *                    RAW, not-yet-encoded password).
     * @param roleId      ID of the role to assign.
     * @param rawPassword Raw password to encode and store.
     */
    SystemUser createUser(SystemUser user, Long roleId, String rawPassword);

    /**
     * Update an existing user's details and role.
     *
     * @param id             ID of the user being edited.
     * @param incoming       Incoming field values (firstName, lastName,
     *                       username, email, phoneNumber).
     * @param roleId         ID of the role to assign.
     * @param rawPassword    New raw password, or null/blank to keep the
     *                       existing password unchanged.
     */
    SystemUser updateUser(Long id, SystemUser incoming, Long roleId, String rawPassword);

    /**
     * Activate or deactivate a user's ability to log in.
     *
     * @param id     ID of the user.
     * @param active true to activate, false to deactivate.
     */
    SystemUser setActive(Long id, boolean active);

    /**
     * Find a user by ID.
     */
    SystemUser findById(Long id);

    /**
     * Find a user by username.
     */
    SystemUser findByUsername(String username);

    /**
     * Get all users, most recently created first.
     */
    List<SystemUser> findAll();

    /**
     * Total number of user accounts (used for the admin dashboard KPI).
     */
    long count();

    /**
     * Check whether a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Check whether an email already exists.
     */
    boolean existsByEmail(String email);
}
