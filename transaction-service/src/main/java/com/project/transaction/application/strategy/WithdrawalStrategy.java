package com.project.transaction.application.strategy;

import com.project.transaction.application.port.out.AccountGatewayPort;
import com.project.transaction.domain.model.Transaction;
import com.project.transaction.domain.vo.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalStrategy implements TransactionStrategy {

    private final AccountGatewayPort accountGateway;

    @Override
    public void execute(Transaction transaction) {
        String sourceAccountId = transaction.sourceAccountId().value();
        BigDecimal amount = transaction.amount().amount();

        log.info("Executing withdrawal strategy for account: {} with amount: {}", sourceAccountId, amount);

        accountGateway.debitAccountBalance(amount, sourceAccountId);

        log.info("Withdrawal completed successfully for account: {}", sourceAccountId);
    }

    @Override
    public boolean supports(TransactionType type) {
        return TransactionType.WITHDRAWAL == type;
    }
}
