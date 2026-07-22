package com.countyassembly.caims.stockout;

import com.countyassembly.caims.department.DepartmentService;
import com.countyassembly.caims.material.MaterialService;
import com.countyassembly.caims.security.CustomUserDetails;
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
    private final MaterialService materialService;
    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("history", stockOutService.findAll());
        model.addAttribute("activePage", "stockOut");

        return "stock-out/list";
    }

    @GetMapping("/new")
    public String showIssueForm(Model model) {

        model.addAttribute("stockOut", new StockOut());
        model.addAttribute("materials", materialService.findAll());
        model.addAttribute("departments", departmentService.findAll());

        return "stock-out/issue-form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("stockOut") StockOut stockOut,
            BindingResult result,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long departmentId,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (materialId == null) {
            result.rejectValue("material", "error.stockOut", "Please select a material.");
        }

        if (departmentId == null) {
            result.rejectValue("department", "error.stockOut", "Please select a department.");
        }

        if (result.hasErrors()) {

            log.debug("Stock out validation errors: {}", result.getAllErrors());

            model.addAttribute("materials", materialService.findAll());
            model.addAttribute("departments", departmentService.findAll());

            return "stock-out/issue-form";
        }

        try {

            stockOutService.issue(stockOut, materialId, departmentId, principal.getUser());

            redirectAttributes.addFlashAttribute("success", "Stock issued successfully.");

        } catch (IllegalStateException ex) {

            // Insufficient stock — send them back to the form rather
            // than losing what they'd entered.
            model.addAttribute("materials", materialService.findAll());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("error", ex.getMessage());

            return "stock-out/issue-form";
        }

        return "redirect:/stock-out";
    }
}
