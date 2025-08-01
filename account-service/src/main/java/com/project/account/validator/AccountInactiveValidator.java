package com.project.account.validator;

import com.project.account.model.Dto.AccountsDto;
import com.project.account.model.Status;
import com.project.account.model.entity.Accounts;
import com.project.account.repository.AccountRepository;
import org.springframework.stereotype.Component;


@Component
public class AccountInactiveValidator extends AccountValidator{

    private AccountRepository accountRepository;

    @Override
    public boolean validate(AccountsDto accountsDto) {
        return accountsDto.getStatus() == Status.INACTIVE;
    }

    @Override
    public void handle(Accounts accounts, AccountsDto accountsDto) {
        accounts.setStatus(Status.ACTIVE);
        accountRepository.save(accounts);
        handleNext(accounts, accountsDto);
    }
}
