package com.bank.domain.exception;

public class BankingException extends RuntimeException {
    
    private final String errorCode;
    private final String details;
    
    public BankingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }
    
    public BankingException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    public BankingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }
    
    public String getErrorCode() { return errorCode; }
    public String getDetails() { return details; }
}