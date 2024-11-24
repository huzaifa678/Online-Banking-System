package com.project.account.exceptions;

public class ClosedAccountException extends RuntimeException{
    public ClosedAccountException(String message) {
        super(message);
    }
}
