package com.countyassembly.caims.department;

import com.countyassembly.caims.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    @NotBlank(message = "Department name is required.")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    @Column(length = 255)
    private String description;

    @Builder.Default
    private Boolean active = true;
}