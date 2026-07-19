package com.countyassembly.caims.StockRequest;

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

import java.time.LocalDateTime;

/**
 * ============================================================
 * StockRequest Entity
 * ============================================================
 *
 * Records a walk-in request for materials on behalf of a
 * Department — e.g. "Paul from ICT requested 5 reams of paper".
 * There is no department-user login; a Storekeeper logs the
 * request on the requester's behalf.
 *
 * Lifecycle:
 *
 *   PENDING  -> logged by a Storekeeper/Admin
 *   APPROVED -> approved by (any) Procurement Officer/Admin
 *   REJECTED -> rejected by Procurement/Admin
 *   ISSUED   -> a Storekeeper recorded a Stock Out against it
 *              (full issue only — see StockOut)
 *
 * Only an APPROVED request can be issued.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stock_requests")
public class StockRequest extends BaseEntity {

    @NotBlank(message = "Requester name is required.")
    @Size(max = 100, message = "Requester name cannot exceed 100 characters.")
    @Column(nullable = false, length = 100)
    private String requesterName;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Column(nullable = false)
    private Integer quantity;

    @Size(max = 255, message = "Notes cannot exceed 255 characters.")
    @Column(length = 255)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StockRequestStatus status = StockRequestStatus.PENDING;

    /**
     * Deliberately not bean-validated — resolved from a plain
     * "materialId" request parameter in the controller.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    /**
     * Deliberately not bean-validated — resolved from a plain
     * "departmentId" request parameter in the controller.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logged_by")
    private SystemUser loggedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private SystemUser approvedBy;

    private LocalDateTime approvedAt;
}