package com.countyassembly.caims.report;

import java.math.BigDecimal;

public record InventoryValuationRow(
        String code,
        String name,
        String unit,
        Integer currentStock,
        BigDecimal unitPrice,
        BigDecimal totalValue
) {
}
