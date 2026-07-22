package com.countyassembly.caims.common.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

/**
 * ============================================================
 * Global Exception Handler
 * ============================================================
 *
 * Centralizes handling of exceptions thrown by controllers/services
 * so that no unhandled exception ever reaches the user as a raw
 * stack trace (Whitelabel Error Page).
 *
 * Any new @Controller in the app automatically benefits from this,
 * no per-controller try/catch needed for these cases.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Missing static file (CSS/JS/image, a stray favicon.ico request,
     * a browser devtools probe, etc). This must NOT fall through to the
     * generic redirect-to-dashboard handler below: a missing stylesheet
     * would then get redirected to the dashboard *page*, which the
     * browser tries to parse as CSS and rejects with a confusing
     * MIME-type error, instead of a plain, obvious 404.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleMissingStaticResource(
            NoResourceFoundException ex,
            HttpServletResponse response) throws IOException {

        log.debug("Static resource not found: {}", ex.getResourcePath());

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Entity not found (e.g. bad/stale id in a URL) -> send the user
     * back to a safe page with a friendly flash message instead of a 500.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {

        log.warn("Resource not found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("error", ex.getMessage());

        return "redirect:/dashboard";
    }

    /**
     * Catch-all safety net for everything else (application logic errors,
     * not missing static files). Logs the real cause for debugging but
     * never exposes internals to the user.
     */
    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, RedirectAttributes redirectAttributes) {

        log.error("Unhandled exception", ex);

        redirectAttributes.addFlashAttribute(
                "error",
                "Something went wrong. Please try again, or contact support if the problem persists.");

        return "redirect:/dashboard";
    }
}
