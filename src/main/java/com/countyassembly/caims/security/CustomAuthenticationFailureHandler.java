package com.countyassembly.caims.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String username = request.getParameter("username");

        // Check if user is already blocked
        if (username != null && loginAttemptService.isBlocked(username)) {
            response.sendRedirect("/login?blocked=true");
            return;
        }

        // Record the failed attempt
        loginAttemptService.loginFailed(username);

        // Redirect with error
        response.sendRedirect("/login?error=true");
    }
}