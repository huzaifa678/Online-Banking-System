package com.project.account.application.port.in;

import java.math.BigDecimal;

/**
 * Driving port for account balance operations.
 */
public interface AccountBalanceUseCase {

    boolean accountBalance(String accountId, BigDecimal transaction);

    /** Preserves legacy behaviour: <em>decreases</em> the balance. */
    BigDecimal creditAccountBalance(BigDecimal amount, String accountId);

    /** Preserves legacy behaviour: <em>increases</em> the balance. */
    BigDecimal debitAccountBalance(BigDecimal amount, String accountId);

    boolean isAccountClosed(String accountId);
}
