package com.project.account.exceptions;

public class SameStatusException extends RuntimeException{
    public SameStatusException(String message) {
        super(message);
    }
}
