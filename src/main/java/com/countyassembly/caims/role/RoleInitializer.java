package com.countyassembly.caims.role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 * Role Initializer
 * ============================================================
 *
 * This class runs automatically when the application starts.
 *
 * Purpose:
 * - Insert default system roles into the database.
 * - Avoid duplicate role creation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer implements CommandLineRunner {

    // Service layer (recommended instead of accessing the repository directly)
    private final RoleService roleService;

    /**
     * This method is executed automatically when Spring Boot finishes starting.
     */
    @Override
    public void run(String... args) {

        createRole(
                "ADMIN",
                "System Administrator with full access"
        );

        createRole(
                "STOREKEEPER",
                "Receives, issues and manages inventory"
        );

        createRole(
                "PROCUREMENT_OFFICER",
                "Manages suppliers and procurement activities"
        );

        createRole(
                "AUDITOR",
                "Views reports and performs inventory audits"
        );

        createRole(
                "MEMBER",
                "Requests and collects items on behalf of a department"
        );
    }

    /**
     * Creates a role only if it doesn't already exist.
     *
     * @param name        Name of the role.
     * @param description Description of the role.
     */
    private void createRole(String name, String description) {

        // Check if the role already exists.
        if (roleService.findByName(name).isEmpty()) {

            // Build a new Role object.
            Role role = Role.builder()
                    .name(name)
                    .description(description)
                    .build();

            // Save the role.
            roleService.save(role);

            log.info("Created role: {}", name);

        } else {

            log.info("Role {} already exists.", name);

        }
    }
}