package com.project.payment.application.port.out;

import java.math.BigDecimal;


public interface AccountGatewayPort {

    boolean doesAccountExists(String accountId);

    boolean accountBalance(String accountId, BigDecimal amount);

    BigDecimal creditAccountBalance(BigDecimal amount, String accountId);

    BigDecimal debitAccountBalance(BigDecimal amount, String accountId);

    boolean isAccountClosed(String accountId);
}
