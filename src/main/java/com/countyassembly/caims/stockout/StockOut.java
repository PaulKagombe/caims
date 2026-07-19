package com.countyassembly.caims.stockout;

import com.countyassembly.caims.common.entity.BaseEntity;
import com.countyassembly.caims.StockRequest.StockRequest;
import com.countyassembly.caims.user.SystemUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * ============================================================
 * StockOut Entity
 * ============================================================
 *
 * The audit record of an issue against an APPROVED StockRequest.
 * Never edited or deleted — together with StockIn, this forms
 * the audit trail for stock movements.
 *
 * quantityIssued always equals the stock request's quantity
 * (full issue only — no partial fulfilment in this version).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stock_outs")
public class StockOut extends BaseEntity {

    @Column(nullable = false)
    private Integer quantityIssued;

    @Column(length = 255)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_request_id", nullable = false)
    private StockRequest stockRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private SystemUser issuedBy;
}