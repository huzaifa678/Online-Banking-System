package com.project.account.validator;

import com.project.account.exceptions.DuplicateAccountTypesException;
import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.Status;
import com.project.account.model.entity.Accounts;
import org.springframework.stereotype.Component;

@Component
public class AccountActiveValidator extends AccountValidator{

    @Override
    public boolean validate(AccountsDto accountsDto) {
        return accountsDto.getStatus() == Status.ACTIVE;
    }

    @Override
    public void handle(Accounts accounts, AccountsDto accountsDto) {
        throw new DuplicateAccountTypesException("Account already exists and is active: " + accountsDto.getUserEmail());
    }

}
