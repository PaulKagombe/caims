package com.countyassembly.caims.restockrequest;

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
@RequestMapping("/restock-requests")
public class RestockRequestController {

    private static final Logger log = LoggerFactory.getLogger(RestockRequestController.class);

    private final RestockRequestService restockRequestService;
    private final MaterialService materialService;
    private final SupplierService supplierService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("requests", restockRequestService.findAll());
        model.addAttribute("activePage", "restockRequests");

        return "restock-requests/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("restockRequest", new RestockRequest());
        model.addAttribute("materials", materialService.findAll());

        return "restock-requests/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("restockRequest") RestockRequest restockRequest,
            BindingResult result,
            @RequestParam(required = false) Long materialId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (materialId == null) {
            result.rejectValue("material", "error.restockRequest", "Please select a material.");
        }

        if (result.hasErrors()) {

            log.debug("Restock request validation errors: {}", result.getAllErrors());

            model.addAttribute("materials", materialService.findAll());

            return "restock-requests/form";
        }

        restockRequestService.create(restockRequest, materialId, principal.getUser());

        redirectAttributes.addFlashAttribute("success",
                "Restock request submitted for Procurement approval.");

        return "redirect:/restock-requests";
    }

    @GetMapping("/approve-form/{id}")
    public String showApproveForm(@PathVariable Long id, Model model) {

        model.addAttribute("restockRequest", restockRequestService.findById(id));
        model.addAttribute("suppliers", supplierService.findAll());

        return "restock-requests/approve-form";
    }

    @PostMapping("/approve/{id}")
    public String approve(
            @PathVariable Long id,
            @RequestParam(required = false) Long supplierId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (supplierId == null) {
            redirectAttributes.addFlashAttribute("error", "Please select a supplier.");
            return "redirect:/restock-requests/approve-form/" + id;
        }

        try {

            restockRequestService.approve(id, supplierId, principal.getUser());

            redirectAttributes.addFlashAttribute("success",
                    "Request approved — a purchase order has been created and is ready to receive.");

        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/restock-requests";
    }

    @PostMapping("/reject/{id}")
    public String reject(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            restockRequestService.reject(id, principal.getUser());
            redirectAttributes.addFlashAttribute("success", "Request rejected.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/restock-requests";
    }
}
