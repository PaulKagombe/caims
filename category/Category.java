package com.countyassembly.caims.category;

import com.countyassembly.caims.common.entity.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "categories")
public class Category extends BaseEntity {

    @NotBlank(message = "Category name is required.")
    @Size(min = 3, max = 100,
            message = "Category name must be between 3 and 100 characters.")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 500,
            message = "Description cannot exceed 500 characters.")
    @Column(length = 500)
    private String description;

    @Builder.Default
    private Boolean active = true;

}