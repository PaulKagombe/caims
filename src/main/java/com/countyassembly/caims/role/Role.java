package com.countyassembly.caims.role;

import com.countyassembly.caims.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a system role.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    /**
     * Name of the role.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Description of the role.
     */
    @Column(length = 255)
    private String description;
}