package com.countyassembly.caims.department;

import com.countyassembly.caims.common.entity.DuplicateResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {

        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("activePage", "departments");

        return "departments/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("department", new Department());

        return "departments/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        model.addAttribute("department", departmentService.findById(id));

        return "departments/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("department") Department department,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "departments/form";
        }

        try {

            if (department.getId() == null) {
                departmentService.create(department);
                redirectAttributes.addFlashAttribute("success", "Department added successfully.");
            } else {
                departmentService.update(department.getId(), department);
                redirectAttributes.addFlashAttribute("success", "Department updated successfully.");
            }

        } catch (DuplicateResourceException ex) {
            result.rejectValue("name", "error.department", ex.getMessage());
            return "departments/form";
        }

        return "redirect:/departments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        departmentService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Department deactivated.");

        return "redirect:/departments";
    }
}