package com.countyassembly.caims.common.entity;

/**
 * Thrown when an operation would violate a uniqueness rule
 * (e.g. creating/renaming a category to a name that is already in use).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
