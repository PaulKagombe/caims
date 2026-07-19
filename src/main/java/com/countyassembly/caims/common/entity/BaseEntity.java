package com.countyassembly.caims.common.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * ============================================================
 * BaseEntity
 * ============================================================
 *
 * Parent class for all entities in CAIMS.
 *
 * Every entity that extends this class automatically gets:
 *  - id
 *  - createdAt
 *  - updatedAt
 *
 * This eliminates duplicate code across entities.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Primary Key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Automatically set when the record is created.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically updated whenever the record changes.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}