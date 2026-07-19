package com.countyassembly.caims.material;

import com.countyassembly.caims.category.Category;
import com.countyassembly.caims.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * ============================================================
 * Material Entity
 * ============================================================
 *
 * Represents a stockable inventory item ("product") that belongs
 * to a Category. currentStock is the on-hand quantity — once
 * Stock In / Stock Out are built, they will be the ones adjusting
 * this value; for now it can be set directly here as a starting
 * balance.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "materials")
public class Material extends BaseEntity {

    /**
     * Stock keeping unit — a short, unique identifying code
     * (e.g. "STA-0001"). Distinct from the database id so it can
     * be meaningful to humans (printed on labels, referenced in
     * conversation) without exposing the internal primary key.
     */
    @NotBlank(message = "Code is required.")
    @Size(max = 50, message = "Code cannot exceed 50 characters.")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Code can only contain letters, numbers, dots, underscores and hyphens."
    )
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Material name is required.")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters.")
    @Column(nullable = false, length = 150)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    @Column(length = 500)
    private String description;

    /**
     * Unit of measure (e.g. PCS, KG, LTR, BOX). Kept as free text
     * rather than an enum since different departments may use
     * different conventions.
     */
    @NotBlank(message = "Unit is required.")
    @Size(max = 20, message = "Unit cannot exceed 20 characters.")
    @Column(nullable = false, length = 20)
    private String unit;

    @NotNull(message = "Unit price is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative.")
    @Digits(integer = 10, fraction = 2, message = "Unit price must be a valid amount.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Size(max = 50, message = "Barcode cannot exceed 50 characters.")
    @Column(length = 50, unique = true)
    private String barcode;

    @NotNull(message = "Current stock is required.")
    @Min(value = 0, message = "Current stock cannot be negative.")
    @Builder.Default
    @Column(nullable = false)
    private Integer currentStock = 0;

    @NotNull(message = "Reorder level is required.")
    @Min(value = 0, message = "Reorder level cannot be negative.")
    @Builder.Default
    @Column(nullable = false)
    private Integer reorderLevel = 0;

    @Builder.Default
    private Boolean active = true;

    /**
     * The category this material belongs to.
     *
     * NOTE: deliberately not bean-validated (@NotNull) here — same
     * reasoning as Role on SystemUser. It's resolved from a plain
     * "categoryId" request parameter and set on the entity in the
     * controller *after* @Valid runs, so at validation time this is
     * always still null for a freshly-bound form object.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Convenience check used by templates/services to flag
     * low-stock items once reorderLevel is meaningfully set.
     */
    @Transient
    public boolean isLowStock() {
        return currentStock != null
                && reorderLevel != null
                && currentStock <= reorderLevel;
    }
}