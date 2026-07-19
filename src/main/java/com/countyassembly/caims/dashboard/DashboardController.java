package com.countyassembly.caims.dashboard;

import com.countyassembly.caims.category.CategoryService;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.PurchaseOrder.PurchaseOrderService;
import com.countyassembly.caims.security.CustomUserDetails;
import com.countyassembly.caims.StockRequest.StockRequestService;
import com.countyassembly.caims.supplier.SupplierService;
import com.countyassembly.caims.user.SystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ============================================================
 * Dashboard Controller
 * ============================================================
 *
 * Displays a dashboard whose content varies by the logged-in
 * user's role. The role is read from the authenticated principal
 * (CustomUserDetails -> SystemUser -> Role) — never from any
 * request parameter — so it can't be spoofed by the client.
 *
 * Only ADMIN gets the full system-wide overview (all module
 * counts, user management shortcut). Other roles get a narrower
 * dashboard relevant to what they're responsible for.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CategoryService categoryService;
    private final MaterialService materialService;
    private final SystemUserService userService;
    private final SupplierService supplierService;
    private final PurchaseOrderService purchaseOrderService;
    private final StockRequestService stockRequestService;

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) Boolean accessDenied,
            Model model) {

        model.addAttribute("activePage", "dashboard");

        if (Boolean.TRUE.equals(accessDenied)) {
            model.addAttribute("error", "You don't have permission to access that page.");
        }

        String roleName = (principal != null
                && principal.getUser() != null
                && principal.getUser().getRole() != null)
                ? principal.getUser().getRole().getName()
                : "MEMBER";

        model.addAttribute("roleName", roleName);

        if ("ADMIN".equals(roleName)) {

            // Full system-wide overview — this is the only role that
            // sees traffic/management-level data across every module.
            model.addAttribute("categoryCount", categoryService.countActive());
            model.addAttribute("materialCount", materialService.countActive());
            model.addAttribute("lowStockCount", materialService.countLowStock());
            model.addAttribute("userCount", userService.count());
            model.addAttribute("supplierCount", supplierService.findAll().size());
            model.addAttribute("pendingPurchaseOrders", purchaseOrderService.findPending().size());
            model.addAttribute("pendingStockRequests", stockRequestService.findPending().size());

        } else if ("STOREKEEPER".equals(roleName)) {

            // Storekeepers need inventory numbers plus what's waiting
            // on them to action (approved POs to receive, approved
            // requests to issue).
            model.addAttribute("categoryCount", categoryService.countActive());
            model.addAttribute("materialCount", materialService.countActive());
            model.addAttribute("lowStockCount", materialService.countLowStock());
            model.addAttribute("awaitingReceipt", purchaseOrderService.findApproved().size());
            model.addAttribute("awaitingIssue", stockRequestService.findApproved().size());

        } else if ("PROCUREMENT_OFFICER".equals(roleName)) {

            // Procurement needs to see what's waiting on their approval.
            model.addAttribute("supplierCount", supplierService.findAll().size());
            model.addAttribute("pendingPurchaseOrders", purchaseOrderService.findPending().size());
            model.addAttribute("pendingStockRequests", stockRequestService.findPending().size());

        }
        // AUDITOR, MEMBER: no module data yet exists that's relevant to
        // them (Reports/Requests aren't built out yet) — their dashboard
        // views are static for now.

        return "dashboard/dashboard";
    }

}
