package com.countyassembly.caims.common.exception;

/**
 * Thrown when a requested entity cannot be found by its identifier.
 * Handled centrally by {@link GlobalExceptionHandler} so controllers
 * never need their own try/catch for this case.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
