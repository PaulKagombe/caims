package com.countyassembly.caims.stockout;

import com.countyassembly.caims.department.DepartmentService;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.security.CustomUserDetails;
import com.countyassembly.caims.StockRequest.StockRequest;
import com.countyassembly.caims.StockRequest.StockRequestService;
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
@RequestMapping("/stock-out")
public class StockOutController {

    private static final Logger log = LoggerFactory.getLogger(StockOutController.class);

    private final StockOutService stockOutService;
    private final StockRequestService stockRequestService;
    private final MaterialService materialService;
    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("pendingRequests", stockRequestService.findPending());
        model.addAttribute("approvedRequests", stockRequestService.findApproved());
        model.addAttribute("history", stockOutService.findAll());
        model.addAttribute("activePage", "stockOut");

        return "stock-out/list";
    }

    @GetMapping("/new-request")
    public String showRequestForm(Model model) {

        model.addAttribute("stockRequest", new StockRequest());
        model.addAttribute("materials", materialService.findAll());
        model.addAttribute("departments", departmentService.findAll());

        return "stock-out/request-form";
    }

    @PostMapping("/request/save")
    public String saveRequest(
            @Valid @ModelAttribute("stockRequest") StockRequest stockRequest,
            BindingResult result,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long departmentId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (materialId == null) {
            result.rejectValue("material", "error.stockRequest", "Please select a material.");
        }

        if (departmentId == null) {
            result.rejectValue("department", "error.stockRequest", "Please select a department.");
        }

        if (result.hasErrors()) {

            log.debug("Stock request validation errors: {}", result.getAllErrors());

            model.addAttribute("materials", materialService.findAll());
            model.addAttribute("departments", departmentService.findAll());

            return "stock-out/request-form";
        }

        stockRequestService.create(stockRequest, materialId, departmentId, principal.getUser());

        redirectAttributes.addFlashAttribute("success",
                "Request logged and sent to Procurement for approval.");

        return "redirect:/stock-out";
    }

    @PostMapping("/issue/{stockRequestId}")
    public String issue(
            @PathVariable Long stockRequestId,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {

            stockOutService.issue(stockRequestId, notes, principal.getUser());

            redirectAttributes.addFlashAttribute("success", "Stock issued successfully.");

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/stock-out";
    }
}