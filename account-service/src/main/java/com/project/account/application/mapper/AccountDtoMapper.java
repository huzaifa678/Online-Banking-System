package com.project.account.application.mapper;

import com.project.account.application.dto.AccountsDto;
import com.project.account.domain.model.Account;
import com.project.account.domain.vo.AccountId;
import com.project.account.domain.vo.Email;
import com.project.account.domain.vo.Money;


public final class AccountDtoMapper {

    private AccountDtoMapper() {
    }

    public static AccountsDto toDto(Account account) {
        return AccountsDto.builder()
                .accountId(account.id().value())
                .accountType(account.type())
                .balance(account.balance().amount())
                .status(account.status())
                .userEmail(account.userEmail().value())
                .build();
    }

    /**
     * Build an aggregate for a brand-new account from an incoming DTO.
     */
    public static Account toNewAccount(AccountsDto dto) {
        return Account.open(
                AccountId.of(dto.getAccountId()),
                dto.getAccountType(),
                dto.getBalance() == null ? Money.ZERO : Money.of(dto.getBalance()),
                Email.of(dto.getUserEmail()));
    }
}
