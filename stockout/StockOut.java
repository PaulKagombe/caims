package com.countyassembly.caims.stockout;

import com.countyassembly.caims.common.entity.BaseEntity;
import com.countyassembly.caims.department.Department;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.user.SystemUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ============================================================
 * StockOut Entity
 * ============================================================
 *
 * The audit record of a walk-in issue: a person from a
 * department collects material(s) from the store, and the
 * storekeeper records who, from where, and how much — no
 * approval workflow. Immutable once created — together with
 * StockIn, this is the audit trail for stock movements.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stock_outs")
public class StockOut extends BaseEntity {

    @NotBlank(message = "Requester's name is required.")
    @Size(max = 100, message = "Requester's name cannot exceed 100 characters.")
    @Column(nullable = false, length = 100)
    private String requesterName;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Column(nullable = false)
    private Integer quantity;

    @Size(max = 255, message = "Notes cannot exceed 255 characters.")
    @Column(length = 255)
    private String notes;

    /**
     * NOTE: deliberately not bean-validated (@NotNull) here — same
     * reasoning used throughout (Role on SystemUser, Category on
     * Material): resolved from a "materialId"/"departmentId" request
     * parameter and set on the entity in the controller *after*
     * @Valid runs, so at validation time these are always still null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private SystemUser issuedBy;
}
