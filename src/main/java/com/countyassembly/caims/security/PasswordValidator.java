package com.countyassembly.caims.security;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern SPECIAL = Pattern.compile("[@$!%*?&]");

    /**
     * Validates a password against the policy.
     * Returns a list of error messages (empty if valid).
     */
    public List<String> validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isBlank()) {
            errors.add("Password is required.");
            return errors;
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long.");
        }

        if (!UPPERCASE.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter (A-Z).");
        }

        if (!LOWERCASE.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter (a-z).");
        }

        if (!DIGIT.matcher(password).find()) {
            errors.add("Password must contain at least one digit (0-9).");
        }

        if (!SPECIAL.matcher(password).find()) {
            errors.add("Password must contain at least one special character (@$!%*?&).");
        }

        return errors;
    }

    /**
     * Quick check if password is valid.
     */
    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }
}