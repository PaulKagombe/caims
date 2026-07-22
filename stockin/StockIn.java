package com.countyassembly.caims.stockin;

import com.countyassembly.caims.common.entity.BaseEntity;
import com.countyassembly.caims.purchaseorder.PurchaseOrder;
import com.countyassembly.caims.user.SystemUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ============================================================
 * StockIn Entity
 * ============================================================
 *
 * The audit record of a received delivery against an APPROVED
 * PurchaseOrder. This record is never edited or deleted — it,
 * together with StockOut, forms the audit trail for stock
 * movements.
 *
 * quantityReceived always equals the purchase order's quantity
 * (full receipt only — no partial deliveries in this version).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stock_ins")
public class StockIn extends BaseEntity {

    @Column(nullable = false)
    private Integer quantityReceived;

    @Column(length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private SystemUser receivedBy;
}
