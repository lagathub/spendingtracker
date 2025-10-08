package com.lagathub.spendingtracker.exception;

/**
 * Exception thrown when a transaction cannot be found by its ID
 * This is a runtime exception that doesn't need to be caught
 */
public class TransactionNotFoundException extends RuntimeException {
    
    public TransactionNotFoundException(String message) {
        super(message);
    }
    
    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    // Convenience constructor for ID-based lookups
    public TransactionNotFoundException(Long id) {
        super("Transaction not found with ID: " + id);
    }
}