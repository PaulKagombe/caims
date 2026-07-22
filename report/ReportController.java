package com.countyassembly.caims.report;

import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.purchaseorder.PurchaseOrder;
import com.countyassembly.caims.purchaseorder.PurchaseOrderStatus;
import com.countyassembly.caims.supplier.SupplierService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReportService reportService;
    private final MaterialService materialService;
    private final SupplierService supplierService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("activePage", "reports");
        return "reports/index";
    }

    @GetMapping("/stock-movements")
    public String stockMovements(
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String export,
            HttpServletResponse response,
            Model model) throws IOException {

        List<StockMovementRow> movements = reportService.getStockMovements(materialId, from, to);

        if ("csv".equals(export)) {

            List<String> headers = List.of("Date", "Type", "Code", "Material", "Quantity",
                    "Counterparty", "Performed By", "Notes");

            List<List<String>> rows = new ArrayList<>();
            for (StockMovementRow r : movements) {
                rows.add(List.of(
                        r.date().format(TS_FORMAT),
                        r.type(),
                        r.materialCode(),
                        r.materialName(),
                        String.valueOf(r.quantity()),
                        r.counterparty(),
                        r.performedBy(),
                        r.notes() != null ? r.notes() : ""
                ));
            }

            CsvExporter.write(response, "stock-movements.csv", headers, rows);
            return null;
        }

        model.addAttribute("movements", movements);
        model.addAttribute("materials", materialService.findAll());
        model.addAttribute("materialId", materialId);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("activePage", "reports");

        return "reports/stock-movements";
    }

    @GetMapping("/purchase-orders")
    public String purchaseOrders(
            @RequestParam(required = false) PurchaseOrderStatus status,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String export,
            HttpServletResponse response,
            Model model) throws IOException {

        List<PurchaseOrder> orders = reportService.getPurchaseOrders(status, supplierId);

        if ("csv".equals(export)) {

            List<String> headers = List.of("Date", "Material", "Supplier", "Quantity",
                    "Status", "Requested By", "Approved By");

            List<List<String>> rows = new ArrayList<>();
            for (PurchaseOrder po : orders) {
                rows.add(List.of(
                        po.getCreatedAt().format(TS_FORMAT),
                        po.getMaterial().getName(),
                        po.getSupplier() != null ? po.getSupplier().getName() : "",
                        String.valueOf(po.getQuantity()),
                        po.getStatus().name(),
                        po.getRequestedBy() != null ? po.getRequestedBy().getUsername() : "",
                        po.getApprovedBy() != null ? po.getApprovedBy().getUsername() : ""
                ));
            }

            CsvExporter.write(response, "purchase-orders.csv", headers, rows);
            return null;
        }

        model.addAttribute("orders", orders);
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("statuses", PurchaseOrderStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("supplierId", supplierId);
        model.addAttribute("activePage", "reports");

        return "reports/purchase-orders";
    }

    @GetMapping("/low-stock")
    public String lowStock(
            @RequestParam(required = false) String export,
            HttpServletResponse response,
            Model model) throws IOException {

        var materials = reportService.getLowStockMaterials();

        if ("csv".equals(export)) {

            List<String> headers = List.of("Code", "Name", "Current Stock", "Reorder Level", "Unit");

            List<List<String>> rows = new ArrayList<>();
            for (var m : materials) {
                rows.add(List.of(
                        m.getCode(),
                        m.getName(),
                        String.valueOf(m.getCurrentStock()),
                        String.valueOf(m.getReorderLevel()),
                        m.getUnit()
                ));
            }

            CsvExporter.write(response, "low-stock.csv", headers, rows);
            return null;
        }

        model.addAttribute("materials", materials);
        model.addAttribute("activePage", "reports");

        return "reports/low-stock";
    }

    @GetMapping("/inventory-valuation")
    public String inventoryValuation(
            @RequestParam(required = false) String export,
            HttpServletResponse response,
            Model model) throws IOException {

        List<InventoryValuationRow> rows = reportService.getInventoryValuation();

        BigDecimal grandTotal = rows.stream()
                .map(InventoryValuationRow::totalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if ("csv".equals(export)) {

            List<String> headers = List.of("Code", "Name", "Unit", "Current Stock", "Unit Price", "Total Value");

            List<List<String>> csvRows = new ArrayList<>();
            for (InventoryValuationRow r : rows) {
                csvRows.add(List.of(
                        r.code(),
                        r.name(),
                        r.unit(),
                        String.valueOf(r.currentStock()),
                        r.unitPrice().toPlainString(),
                        r.totalValue().toPlainString()
                ));
            }

            CsvExporter.write(response, "inventory-valuation.csv", headers, csvRows);
            return null;
        }

        model.addAttribute("rows", rows);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("activePage", "reports");

        return "reports/inventory-valuation";
    }
}
