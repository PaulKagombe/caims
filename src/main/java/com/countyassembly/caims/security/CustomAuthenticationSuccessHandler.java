package com.countyassembly.caims.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ============================================================
 * CustomAuthenticationSuccessHandler
 * ============================================================
 *
 * Handles successful authentication and redirects to dashboard.
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        log.info("Authentication successful for user: {}", authentication.getName());

        // Redirect to dashboard
        response.sendRedirect("/dashboard");
    }
}

