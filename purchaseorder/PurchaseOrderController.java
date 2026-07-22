package com.countyassembly.caims.purchaseorder;

import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.security.CustomUserDetails;
import com.countyassembly.caims.supplier.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderController.class);

    private final PurchaseOrderService purchaseOrderService;
    private final MaterialService materialService;
    private final SupplierService supplierService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("orders", purchaseOrderService.findAll());
        model.addAttribute("activePage", "purchaseOrders");

        return "purchase-orders/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("purchaseOrder", new PurchaseOrder());
        model.addAttribute("materials", materialService.findAll());
        model.addAttribute("suppliers", supplierService.findAll());

        return "purchase-orders/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("purchaseOrder") PurchaseOrder purchaseOrder,
            BindingResult result,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long supplierId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (materialId == null) {
            result.rejectValue("material", "error.purchaseOrder", "Please select a material.");
        }

        if (supplierId == null) {
            result.rejectValue("supplier", "error.purchaseOrder", "Please select a supplier.");
        }

        if (result.hasErrors()) {

            log.debug("Purchase order validation errors: {}", result.getAllErrors());

            model.addAttribute("materials", materialService.findAll());
            model.addAttribute("suppliers", supplierService.findAll());

            return "purchase-orders/form";
        }

        purchaseOrderService.create(purchaseOrder, materialId, supplierId, principal.getUser());

        redirectAttributes.addFlashAttribute("success",
                "Purchase order submitted and awaiting Procurement approval.");

        // Storekeeper doesn't have access to the full order list (that's
        // approval-related, procurement/admin only) — send them back to
        // Stock In, where their order will appear once it's approved.
        boolean isStorekeeper = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STOREKEEPER"));

        return isStorekeeper ? "redirect:/stock-in" : "redirect:/purchase-orders";
    }

    @PostMapping("/approve/{id}")
    public String approve(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            purchaseOrderService.approve(id, principal.getUser());
            redirectAttributes.addFlashAttribute("success", "Purchase order approved.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/purchase-orders";
    }

    @PostMapping("/reject/{id}")
    public String reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            purchaseOrderService.reject(id, principal.getUser());
            redirectAttributes.addFlashAttribute("success", "Purchase order rejected.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/purchase-orders";
    }
}
