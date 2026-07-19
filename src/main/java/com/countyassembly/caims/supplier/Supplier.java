package com.countyassembly.caims.supplier;

import com.countyassembly.caims.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
@Table(name = "suppliers")
public class Supplier extends BaseEntity {

    @NotBlank(message = "Supplier name is required.")
    @Size(min = 2, max = 150, message = "Supplier name must be between 2 and 150 characters.")
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Size(max = 100, message = "Contact person cannot exceed 100 characters.")
    @Column(length = 100)
    private String contactPerson;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters.")
    @Column(length = 20)
    private String phoneNumber;

    @Email(message = "Enter a valid email address.")
    @Size(max = 150, message = "Email cannot exceed 150 characters.")
    @Column(length = 150)
    private String email;

    @Size(max = 255, message = "Address cannot exceed 255 characters.")
    @Column(length = 255)
    private String address;

    @Builder.Default
    private Boolean active = true;
}