package com.project.account.validator;

import com.project.account.exceptions.ClosedAccountException;
import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.Status;
import com.project.account.model.entity.Accounts;
import org.springframework.stereotype.Component;

@Component
public class AccountClosedValidator extends AccountValidator{

    @Override
    public boolean validate(AccountsDto accountsDto) {
        return accountsDto.getStatus() == Status.CLOSED;
    }

    @Override
    public void handle(Accounts accounts, AccountsDto accountsDto) {
        throw new ClosedAccountException("Account is closed for user: " + accountsDto.getUserEmail());
    }
}
