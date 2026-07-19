package com.countyassembly.caims.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Role} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
@Repository // Indicates that this interface is a "Repository"
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a {@link Role} by its name.
     *
     * @param name The name of the role to find.
     * @return An {@link Optional} containing the found role, or empty if not found.
     */
    Optional<Role> findByName(String name);

    /**
     * Checks if a role with the given name exists.
     *
     * @param name The name of the role to check.
     * @return true if a role with the given name exists, false otherwise.
     */
    boolean existsByName(String name);
}
