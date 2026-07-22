package com.countyassembly.caims.user;

import com.countyassembly.caims.common.entity.BaseEntity;
import com.countyassembly.caims.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ============================================================
 * SystemUser Entity
 * ============================================================
 *
 * Represents a user who can log into CAIMS.
 *
 * Every user:
 *  - has one Role
 *  - can perform inventory operations
 *  - has an encrypted password
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(
        name = "system_users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
public class SystemUser extends BaseEntity {

    /**
     * User's first name.
     */
    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters.")
    @Column(nullable = false, length = 100)
    private String firstName;

    /**
     * User's last name.
     */
    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters.")
    @Column(nullable = false, length = 100)
    private String lastName;

    /**
     * Username used during login.
     */
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username can only contain letters, numbers, dots, underscores and hyphens."
    )
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * Official email.
     */
    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 150, message = "Email cannot exceed 150 characters.")
    @Column(nullable = false, length = 150)
    private String email;

    /**
     * BCrypt encrypted password.
     *
     * Never store plain text passwords.
     *
     * NOTE: deliberately has no @NotBlank/@Size here. This field is reused
     * as the request-binding target for both "create user" (password
     * required, raw) and "edit user" (password optional — blank means
     * "keep existing"). Bean Validation can't express "required only
     * sometimes" without validation groups, so the create/edit password
     * rules are enforced explicitly in the controller and service instead.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Phone number.
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters.")
    @Column(length = 20)
    private String phoneNumber;

    /**
     * Determines whether the account can log in.
     */
    @Builder.Default
    private Boolean active = true;

    /**
     * Every user has exactly one role.
     *
     * Example:
     * Paul -> ADMIN
     * Mary -> STOREKEEPER
     *
     * NOTE: deliberately not bean-validated (@NotNull) here. The role is
     * resolved from a plain "roleId" request parameter and set on the
     * entity in the controller *after* @Valid runs — at validation time
     * this field is always still null for a freshly-bound form object,
     * so a @NotNull here would reject every single submission.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}