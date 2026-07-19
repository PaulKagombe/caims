package com.countyassembly.caims.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * ============================================================
 * JPA Audit Configuration
 * ============================================================
 *
 * Enables automatic population of:
 *  - @CreatedDate
 *  - @LastModifiedDate
 *
 * throughout the application.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}