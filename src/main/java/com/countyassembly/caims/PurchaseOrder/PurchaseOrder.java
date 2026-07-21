package com.countyassembly.caims.PurchaseOrder;

import com.countyassembly.caims.common.entity.BaseEntity;
import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.supplier.Supplier;
import com.countyassembly.caims.user.SystemUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * ============================================================
 * PurchaseOrder Entity
 * ============================================================
 *
 * A request from Procurement to order a quantity of a Material
 * from a Supplier. Lifecycle:
 *
 *   PENDING  -> created by Procurement/Admin
 *   APPROVED -> approved by (any) Procurement Officer/Admin
 *   REJECTED -> rejected by Procurement/Admin
 *   RECEIVED -> a Storekeeper recorded a Stock In against it
 *              (full receipt only — see StockIn)
 *
 * Only an APPROVED order can be received.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder extends BaseEntity {

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
    private PurchaseOrderStatus status = PurchaseOrderStatus.PENDING;

    /**
     * Deliberately not bean-validated — resolved from a plain
     * "materialId" request parameter in the controller.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    /**
     * Deliberately not bean-validated — resolved from a plain
     * "supplierId" request parameter in the controller.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private SystemUser requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private SystemUser approvedBy;

    private LocalDateTime approvedAt;
}
