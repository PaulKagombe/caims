package com.countyassembly.caims.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================
 * SystemUser Repository
 * ============================================================
 *
 * Provides CRUD operations and custom queries
 * for the SystemUser entity.
 */
@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    /**
     * Find a user by username with role eagerly loaded.
     *
     * Used during authentication.
     *
     * @param username Username entered during login.
     * @return Optional containing the user if found.
     */
    @Query("SELECT u FROM SystemUser u LEFT JOIN FETCH u.role WHERE u.username = :username")
    Optional<SystemUser> findByUsername(@Param("username") String username);

    /**
     * Check if a username already exists.
     *
     * Used when creating a new user.
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email already exists.
     *
     * Prevents duplicate email addresses.
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by email.
     *
     * Useful for password reset functionality.
     */
    Optional<SystemUser> findByEmail(String email);

    /**
     * All users, most recently created first — used for the admin
     * user management list.
     */
    java.util.List<SystemUser> findAllByOrderByCreatedAtDesc();
}