package com.countyassembly.caims.config;

import com.countyassembly.caims.security.CustomUserDetailsService;
import com.countyassembly.caims.security.CustomAuthenticationSuccessHandler;
import com.countyassembly.caims.security.CustomAuthenticationFailureHandler;
import com.countyassembly.caims.security.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================
 * Security Configuration
 * ============================================================
 *
 * Configures authentication and authorization for CAIMS.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    /**
     * Service that loads users from the database.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Custom success handler.
     */
    private final CustomAuthenticationSuccessHandler successHandler;

    /**
     * Custom failure handler.
     */
    private final CustomAuthenticationFailureHandler failureHandler;

    /**
     * Custom access denied handler (403s).
     */
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * BCrypt password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                // Disable CSRF during development
                .csrf(csrf -> csrf.disable())

                // Configure URL authorization
                .authorizeHttpRequests(auth -> auth

                        // Public resources
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/login",
                                "/login?logout"
                        ).permitAll()

                        // User management is admin-only
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Materials (inventory) — admin and storekeeper only
                        .requestMatchers("/materials/**").hasAnyRole("ADMIN", "STOREKEEPER")

                        // Suppliers — admin and procurement only
                        .requestMatchers("/suppliers/**").hasAnyRole("ADMIN", "PROCUREMENT_OFFICER")

                        // Departments — admin manages the department list
                        .requestMatchers("/departments/**").hasRole("ADMIN")

                        // Purchase Orders — created and approved by procurement (or admin);
                        // also where pending Stock Requests are approved/rejected
                        .requestMatchers("/purchase-orders/**").hasAnyRole("ADMIN", "PROCUREMENT_OFFICER")

                        // Stock In / Stock Out — executed by the storekeeper (or admin)
                        .requestMatchers("/stock-in/**").hasAnyRole("ADMIN", "STOREKEEPER")
                        .requestMatchers("/stock-out/**").hasAnyRole("ADMIN", "STOREKEEPER")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Configure form login
                .formLogin(form -> form

                        // Use our custom login page
                        .loginPage("/login")

                        // URL that processes login
                        .loginProcessingUrl("/login")

                        // Use custom success handler
                        .successHandler(successHandler)

                        // Use custom failure handler
                        .failureHandler(failureHandler)

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?logout")

                        .invalidateHttpSession(true)

                        .clearAuthentication(true)

                        .permitAll()
                )

                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // Configure session management
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(1)
                        )
                )

                // Register our database user service
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
