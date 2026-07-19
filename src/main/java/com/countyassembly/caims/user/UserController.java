package com.countyassembly.caims.user;

import com.countyassembly.caims.common.entity.DuplicateResourceException;
import com.countyassembly.caims.role.RoleService;
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

/**
 * ============================================================
 * User Controller
 * ============================================================
 *
 * Lets a System Administrator create, edit, and activate/deactivate
 * other users, assigning them a role. Restricted to ROLE_ADMIN via
 * SecurityConfig ("/users/**" -> hasRole("ADMIN")).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final SystemUserService userService;
    private final RoleService roleService;

    @GetMapping
    public String listUsers(Model model) {

        model.addAttribute("users", userService.findAll());
        model.addAttribute("activePage", "users");

        return "users/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {

        model.addAttribute("user", new SystemUser());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("isNew", true);

        return "users/form";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {

        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("isNew", false);

        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(
            @Valid @ModelAttribute("user") SystemUser user,
            BindingResult result,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean isNew = (user.getId() == null);

        // Password is required on create, optional on edit (blank = keep
        // existing). Bean Validation can't express this on the entity
        // itself, so it's checked explicitly here before anything else.
        if (isNew && (password == null || password.isBlank())) {
            result.rejectValue("password", "error.user", "Password is required for a new user.");
        }

        if (roleId == null) {
            result.rejectValue("role", "error.user", "Please select a role.");
        }

        if (result.hasErrors()) {

            log.debug("User form validation errors: {}", result.getAllErrors());

            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("isNew", isNew);

            return "users/form";
        }

        try {

            if (isNew) {

                userService.createUser(user, roleId, password);

                redirectAttributes.addFlashAttribute("success", "User created successfully.");

            } else {

                userService.updateUser(user.getId(), user, roleId, password);

                redirectAttributes.addFlashAttribute("success", "User updated successfully.");
            }

        } catch (DuplicateResourceException ex) {

            result.rejectValue("username", "error.user", ex.getMessage());

            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("isNew", isNew);

            return "users/form";
        }

        return "redirect:/users";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal,
            RedirectAttributes redirectAttributes) {

        if (isSelf(id, principal)) {

            redirectAttributes.addFlashAttribute(
                    "error", "You cannot deactivate your own account.");

            return "redirect:/users";
        }

        userService.setActive(id, false);

        redirectAttributes.addFlashAttribute("success", "User deactivated.");

        return "redirect:/users";
    }

    @PostMapping("/activate/{id}")
    public String activateUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        userService.setActive(id, true);

        redirectAttributes.addFlashAttribute("success", "User activated.");

        return "redirect:/users";
    }

    private boolean isSelf(Long targetId, CustomUserDetails principal) {

        return principal != null
                && principal.getUser() != null
                && principal.getUser().getId().equals(targetId);
    }
}