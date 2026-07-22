package com.countyassembly.caims.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ============================================================
 * CustomAccessDeniedHandler
 * ============================================================
 *
 * Without this, an authenticated user hitting a route their role
 * isn't permitted (e.g. a Member navigating to /users) would see
 * Spring's raw Whitelabel 403 error page. Instead, send them back
 * to the dashboard with a friendly message.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.sendRedirect(request.getContextPath() + "/dashboard?accessDenied=true");
    }
}
