package com.project.account.validator;

import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.entity.Accounts;

public abstract class AccountValidator {

    protected AccountValidator nextValidator;

    public void setNext(AccountValidator validator) {
        this.nextValidator = validator;
    }

    public abstract boolean validate(AccountsDto accountsDto);

    public abstract void handle(Accounts accounts, AccountsDto accountsDto);

    protected void handleNext(Accounts accounts, AccountsDto accountsDto) {
        if (nextValidator != null) {
            if (nextValidator.validate(accountsDto)) {
                nextValidator.handle(accounts, accountsDto);
            } else {
                nextValidator.handleNext(accounts, accountsDto);
            }
        }
    }

}
