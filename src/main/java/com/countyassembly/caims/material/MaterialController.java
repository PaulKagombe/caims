package com.countyassembly.caims.material;

import com.countyassembly.caims.category.CategoryService;
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
@RequestMapping("/materials")
public class MaterialController {

    private static final Logger log = LoggerFactory.getLogger(MaterialController.class);

    private final MaterialService materialService;
    private final CategoryService categoryService;

    @GetMapping
    public String listMaterials(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Material> materialPage = materialService.search(keyword, categoryId, page);

        model.addAttribute("materials", materialPage.getContent());
        model.addAttribute("materialPage", materialPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("activePage", "materials");

        return "materials/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("material", new Material());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isNew", true);

        return "materials/form";
    }

    @GetMapping("/edit/{id}")
    public String editMaterial(@PathVariable Long id, Model model) {

        model.addAttribute("material", materialService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isNew", false);

        return "materials/form";
    }

    @PostMapping("/save")
    public String saveMaterial(
            @Valid @ModelAttribute("material") Material material,
            BindingResult result,
            @RequestParam(required = false) Long categoryId,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (categoryId == null) {
            result.rejectValue("category", "error.material", "Please select a category.");
        }

        if (result.hasErrors()) {

            log.debug("Material form validation errors: {}", result.getAllErrors());

            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isNew", material.getId() == null);

            return "materials/form";
        }

        try {

            if (material.getId() == null) {

                materialService.create(material, categoryId);

                redirectAttributes.addFlashAttribute("success", "Material added successfully.");

            } else {

                materialService.update(material.getId(), material, categoryId);

                redirectAttributes.addFlashAttribute("success", "Material updated successfully.");
            }

        } catch (DuplicateResourceException ex) {

            result.rejectValue("code", "error.material", ex.getMessage());

            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isNew", material.getId() == null);

            return "materials/form";
        }

        return "redirect:/materials";
    }

    @PostMapping("/delete/{id}")
    public String deleteMaterial(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        materialService.delete(id);

        redirectAttributes.addFlashAttribute("success", "Material deactivated.");

        return "redirect:/materials";
    }

}