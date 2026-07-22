package com.countyassembly.caims.restockrequest;

import com.countyassembly.caims.common.entity.BaseEntity;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.purchaseorder.PurchaseOrder;
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
 * RestockRequest Entity
 * ============================================================
 *
 * A Storekeeper's flag that a material needs reordering — no
 * supplier is chosen here, that's Procurement's call. Lifecycle:
 *
 *   PENDING  -> created by Storekeeper/Admin
 *   APPROVED -> Procurement/Admin picks a supplier and approves;
 *               this simultaneously creates an APPROVED
 *               PurchaseOrder (see purchaseOrder field)
 *   REJECTED -> Procurement/Admin declines; no order is created
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "restock_requests")
public class RestockRequest extends BaseEntity {

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Column(nullable = false)
    private Integer quantity;

    @NotBlank(message = "Please give a reason for this request.")
    @Size(max = 255, message = "Reason cannot exceed 255 characters.")
    @Column(nullable = false, length = 255)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RestockRequestStatus status = RestockRequestStatus.PENDING;

    /**
     * Deliberately not bean-validated — resolved from a plain
     * "materialId" request parameter in the controller.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private SystemUser requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private SystemUser reviewedBy;

    private LocalDateTime reviewedAt;

    /**
     * The Purchase Order created when this request was approved.
     * Null until (and unless) approved — kept for traceability so a
     * Storekeeper can see exactly what their request became.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;
}
