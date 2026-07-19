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

    SystemUser createUser(SystemUser user, Long roleId, String rawPassword);
    SystemUser updateUser(Long id, SystemUser incoming, Long roleId, String rawPassword);

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