package com.project.transaction.application.validation;

import com.project.transaction.application.dto.TransactionDto;


public abstract class TransactionValidator {

    protected TransactionValidator nextValidator;

    public void setNext(TransactionValidator validator) {
        this.nextValidator = validator;
    }

    public abstract void validate(TransactionDto transactionDto);

    protected void validateNext(TransactionDto transactionDto) {
        if (nextValidator != null) {
            nextValidator.validate(transactionDto);
        }
    }
}
