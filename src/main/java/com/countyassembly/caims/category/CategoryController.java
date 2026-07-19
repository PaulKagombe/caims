package com.countyassembly.caims.category;

import com.countyassembly.caims.common.entity.DuplicateResourceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Category> categoryPage = categoryService.search(keyword, page);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "categories");

        return "categories/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("category", new Category());

        return "categories/form";
    }

    @PostMapping("/save")
    public String saveCategory(
            @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            log.debug("Category form validation errors: {}", result.getAllErrors());

            return "categories/form";
        }

        try {

            if (category.getId() == null) {

                categoryService.save(category);

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Category added successfully.");

            } else {

                categoryService.update(category.getId(), category);

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Category updated successfully.");
            }

        } catch (DuplicateResourceException ex) {

            // Business validation error the user can fix directly on the form,
            // so we handle it here rather than letting it bubble to the
            // global handler (which would redirect away and lose their input).
            result.rejectValue("name", "error.category", ex.getMessage());

            return "categories/form";
        }

        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {

        // If id doesn't exist, CategoryService throws ResourceNotFoundException,
        // which GlobalExceptionHandler turns into a safe redirect + flash message
        // instead of a crash.
        model.addAttribute("category", categoryService.findById(id));

        return "categories/form";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        categoryService.delete(id);

        redirectAttributes.addFlashAttribute("success", "Category deleted successfully.");

        return "redirect:/categories";
    }

}
