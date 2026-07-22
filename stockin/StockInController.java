package com.countyassembly.caims.stockin;

import com.countyassembly.caims.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.countyassembly.caims.purchaseorder.PurchaseOrderService;

@Controller
@RequiredArgsConstructor
public class StockInController {

    private final StockInService stockInService;
    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/stock-in")
    public String list(Model model) {

        model.addAttribute("approvedOrders", purchaseOrderService.findApproved());
        model.addAttribute("history", stockInService.findAll());
        model.addAttribute("activePage", "stockIn");

        return "stock-in/list";
    }

    @PostMapping("/stock-in/receive/{purchaseOrderId}")
    public String receive(
            @PathVariable Long purchaseOrderId,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {

            stockInService.receive(purchaseOrderId, notes, principal.getUser());

            redirectAttributes.addFlashAttribute("success",
                    "Stock received and added to inventory.");

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/stock-in";
    }
}
