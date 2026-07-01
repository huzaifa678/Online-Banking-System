package com.project.account.application.port.in;

import com.project.account.application.dto.AccountsDto;
import com.project.account.domain.vo.AccountStatus;

/**
 * Driving port for state-changing account operations.
 */
public interface AccountCommandUseCase {

    void addAccount(AccountsDto accountsDto);

    void closeAccount(String accountId);

    void updateStatus(String accountId, AccountStatus status);

    AccountsDto updateAccount(String accountId, AccountsDto accountsDto);

    void deleteAccount(String accountId);
}
