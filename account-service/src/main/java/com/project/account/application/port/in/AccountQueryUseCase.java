package com.project.account.application.port.in;

import com.project.account.application.dto.AccountsDto;

import java.util.List;

/**
 * Driving port for read-only account queries.
 */
public interface AccountQueryUseCase {

    AccountsDto getAccountById(String accountId);

    List<AccountsDto> getAllAccounts();

    boolean doesAccountExists(String accountId);
}
