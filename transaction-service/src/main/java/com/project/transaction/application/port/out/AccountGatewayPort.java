package com.project.transaction.application.port.out;

import java.math.BigDecimal;

/**
 * Driven port (anti-corruption layer) to the account service. Implemented by a
 * REST client adapter. Method semantics mirror the account service's internal
 * API — note {@code creditAccountBalance} decreases and {@code debitAccountBalance}
 * increases the remote balance (behaviour preserved from the original client).
 */
public interface AccountGatewayPort {

    boolean doesAccountExists(String accountId);

    boolean accountBalance(String accountId, BigDecimal amount);

    BigDecimal creditAccountBalance(BigDecimal amount, String accountId);

    BigDecimal debitAccountBalance(BigDecimal amount, String accountId);

    boolean isAccountClosed(String accountId);
}
