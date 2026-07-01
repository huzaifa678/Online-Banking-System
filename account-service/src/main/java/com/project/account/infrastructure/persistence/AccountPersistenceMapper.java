package com.project.account.infrastructure.persistence;

import com.project.account.domain.model.Account;
import com.project.account.domain.vo.AccountId;
import com.project.account.domain.vo.Email;
import com.project.account.domain.vo.Money;


final class AccountPersistenceMapper {

    private AccountPersistenceMapper() {
    }

    static Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                AccountId.of(entity.getAccountId()),
                entity.getAccountType(),
                Money.of(entity.getBalance()),
                entity.getStatus(),
                Email.of(entity.getUserEmail()));
    }

    static AccountEntity toEntity(Account account) {
        return AccountEntity.builder()
                .accountId(account.id().value())
                .accountType(account.type())
                .balance(account.balance().amount())
                .status(account.status())
                .userEmail(account.userEmail().value())
                .build();
    }
}
