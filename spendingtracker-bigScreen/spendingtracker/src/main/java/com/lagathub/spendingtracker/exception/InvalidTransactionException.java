package com.lagathub.spendingtracker.exception;

/*
 * Custom exception for invalid transaction operations
 * This is a runtime exception, so it doesn't need to be caught
 */
public class InvalidTransactionException extends RuntimeException {
	
	/*
	 * Constructor with message only
	 */
	public InvalidTransactionException(String message) {
		super(message);		
	}
	
	/*
	 * Constructor with message and cause
	 */
	public InvalidTransactionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/*
	 * Constructor for validation errors with specific field
	 */
	public InvalidTransactionException(String field, Object value, String reason) {
		super(String.format("Invalid %s: '%s' -%s", field, value, reason));
	}

}
