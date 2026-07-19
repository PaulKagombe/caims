package com.countyassembly.caims.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ============================================================
 * Login Controller
 * ============================================================
 *
 * Handles requests for:
 * - Login page
 * - Root URL
 * - Dashboard redirect
 */
@Controller
public class LoginController {

    /**
     * Display the custom login page.
     *
     * If the user is already authenticated,
     * redirect them to the dashboard.
     */
    @GetMapping("/login")
    public String login(Authentication authentication) {

        // If user is already authenticated, redirect to dashboard
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            return "redirect:/dashboard";
        }

        // Display login form (error/logout messages handled by Thymeleaf)
        return "login";
    }

    /**
     * Redirect the root URL to the dashboard.
     */
    @GetMapping("/")
    public String home() {

        return "redirect:/dashboard";
    }
}