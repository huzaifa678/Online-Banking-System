package com.project.transaction.infrastructure.client;

import com.project.transaction.application.port.out.AccountGatewayPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class AccountGatewayAdapter implements AccountGatewayPort {

    private final AccountClient accountClient;

    @Override
    public boolean doesAccountExists(String accountId) {
        return accountClient.doesAccountExists(accountId);
    }

    @Override
    public boolean accountBalance(String accountId, BigDecimal amount) {
        return accountClient.accountBalance(accountId, amount);
    }

    @Override
    public BigDecimal creditAccountBalance(BigDecimal amount, String accountId) {
        return accountClient.creditAccountBalance(amount, accountId);
    }

    @Override
    public BigDecimal debitAccountBalance(BigDecimal amount, String accountId) {
        return accountClient.debitAccountBalance(amount, accountId);
    }

    @Override
    public boolean isAccountClosed(String accountId) {
        return accountClient.isAccountClosed(accountId);
    }
}
