package com.countyassembly.caims.report;

import com.countyassembly.caims.material.Material;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.PurchaseOrder.PurchaseOrder;
import com.countyassembly.caims.PurchaseOrder.PurchaseOrderService;
import com.countyassembly.caims.PurchaseOrder.PurchaseOrderStatus;
import com.countyassembly.caims.StockIn.StockIn;
import com.countyassembly.caims.StockIn.StockInService;
import com.countyassembly.caims.stockout.StockOut;
import com.countyassembly.caims.stockout.StockOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ============================================================
 * Report Service
 * ============================================================
 *
 * Read-only aggregation over existing modules — reports don't
 * persist anything of their own, they just combine, filter, and
 * present data that already exists.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final StockInService stockInService;
    private final StockOutService stockOutService;
    private final MaterialService materialService;
    private final PurchaseOrderService purchaseOrderService;

    public List<StockMovementRow> getStockMovements(Long materialId, LocalDate from, LocalDate to) {

        String materialCode = materialId != null
                ? materialService.findById(materialId).getCode()
                : null;

        Stream<StockMovementRow> inRows = stockInService.findAll().stream()
                .map(this::toRow);

        Stream<StockMovementRow> outRows = stockOutService.findAll().stream()
                .map(this::toRow);

        return Stream.concat(inRows, outRows)
                .filter(row -> materialCode == null || materialCode.equals(row.materialCode()))
                .filter(row -> from == null || !row.date().toLocalDate().isBefore(from))
                .filter(row -> to == null || !row.date().toLocalDate().isAfter(to))
                .sorted(Comparator.comparing(StockMovementRow::date).reversed())
                .collect(Collectors.toList());
    }

    public List<PurchaseOrder> getPurchaseOrders(PurchaseOrderStatus status, Long supplierId) {

        return purchaseOrderService.findAll().stream()
                .filter(po -> status == null || po.getStatus() == status)
                .filter(po -> supplierId == null
                        || (po.getSupplier() != null && supplierId.equals(po.getSupplier().getId())))
                .collect(Collectors.toList());
    }

    public List<Material> getLowStockMaterials() {
        return materialService.findLowStock();
    }

    public List<InventoryValuationRow> getInventoryValuation() {

        return materialService.findAll().stream()
                .map(m -> new InventoryValuationRow(
                        m.getCode(),
                        m.getName(),
                        m.getUnit(),
                        m.getCurrentStock(),
                        m.getUnitPrice(),
                        m.getUnitPrice().multiply(BigDecimal.valueOf(m.getCurrentStock()))
                ))
                .sorted(Comparator.comparing(InventoryValuationRow::totalValue).reversed())
                .collect(Collectors.toList());
    }

    private StockMovementRow toRow(StockIn stockIn) {

        PurchaseOrder order = stockIn.getPurchaseOrder();

        return new StockMovementRow(
                stockIn.getCreatedAt(),
                "IN",
                order.getMaterial().getCode(),
                order.getMaterial().getName(),
                stockIn.getQuantityReceived(),
                order.getSupplier() != null ? order.getSupplier().getName() : "—",
                stockIn.getReceivedBy() != null ? stockIn.getReceivedBy().getUsername() : "—",
                stockIn.getNotes()
        );
    }

    private StockMovementRow toRow(StockOut stockOut) {

        return new StockMovementRow(
                stockOut.getCreatedAt(),
                "OUT",
                stockOut.getMaterial().getCode(),
                stockOut.getMaterial().getName(),
                stockOut.getQuantity(),
                stockOut.getDepartment() != null ? stockOut.getDepartment().getName() : "—",
                stockOut.getIssuedBy() != null ? stockOut.getIssuedBy().getUsername() : "—",
                stockOut.getNotes()
        );
    }
}
