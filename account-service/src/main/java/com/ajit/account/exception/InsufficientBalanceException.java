package com.ajit.account.exception;

/**
 * The type Insufficient balance exception.
 */
public class InsufficientBalanceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Insufficient balance exception.
     *
     * @param message the message
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }

}
