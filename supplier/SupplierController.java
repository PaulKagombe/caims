package com.countyassembly.caims.supplier;

import com.countyassembly.caims.common.exception.DuplicateResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("activePage", "suppliers");

        return "suppliers/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("supplier", new Supplier());

        return "suppliers/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        model.addAttribute("supplier", supplierService.findById(id));

        return "suppliers/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("supplier") Supplier supplier,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "suppliers/form";
        }

        try {

            if (supplier.getId() == null) {
                supplierService.create(supplier);
                redirectAttributes.addFlashAttribute("success", "Supplier added successfully.");
            } else {
                supplierService.update(supplier.getId(), supplier);
                redirectAttributes.addFlashAttribute("success", "Supplier updated successfully.");
            }

        } catch (DuplicateResourceException ex) {
            result.rejectValue("name", "error.supplier", ex.getMessage());
            return "suppliers/form";
        }

        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        supplierService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Supplier deactivated.");

        return "redirect:/suppliers";
    }
}
