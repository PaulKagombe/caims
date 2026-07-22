package com.countyassembly.caims.report;

import java.time.LocalDateTime;

/**
 * A single row in the combined Stock Movement report — either a
 * receipt (IN, against a Purchase Order/Supplier) or an issue
 * (OUT, to a Department). Read-only projection, never persisted.
 */
public record StockMovementRow(
        LocalDateTime date,
        String type,               // "IN" or "OUT"
        String materialCode,
        String materialName,
        Integer quantity,
        String counterparty,       // Supplier name (IN) or Department name (OUT)
        String performedBy,
        String notes
) {
}
