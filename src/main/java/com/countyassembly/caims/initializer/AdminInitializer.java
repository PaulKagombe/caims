package com.countyassembly.caims.initializer;

import com.countyassembly.caims.role.Role;
import com.countyassembly.caims.role.RoleRepository;
import com.countyassembly.caims.user.SystemUser;
import com.countyassembly.caims.user.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 * Admin Initializer
 * ============================================================
 *
 * Creates the default administrator account when the
 * application starts for the first time.
 *
 * If the admin user already exists,
 * nothing will be created.
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final SystemUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Prevent creating duplicate admin accounts
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        // Retrieve the ADMIN role
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() ->
                        new RuntimeException("ADMIN role not found."));

        // Create the administrator account
        SystemUser admin = SystemUser.builder()
                .firstName("System")
                .lastName("Administrator")
                .username("admin")
                .email("admin@caims.local")
                .phoneNumber("0700000000")
                .password(passwordEncoder.encode("admin123"))
                .active(true)
                .role(adminRole)
                .build();

        // Save to the database
        userRepository.save(admin);

        System.out.println("==========================================");
        System.out.println(" CAIMS Administrator Account Created");
        System.out.println(" Username : admin");
        System.out.println(" Password : admin123");
        System.out.println("==========================================");
    }
}