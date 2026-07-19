package com.countyassembly.caims.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ============================================================
 * CustomAuthenticationFailureHandler
 * ============================================================
 *
 * Handles failed authentication and redirects to login.
 */
@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        log.warn("Authentication failed: {}", exception.getMessage());

        // Redirect to login page with error parameter
        response.sendRedirect("/login?error=true");
    }
}

