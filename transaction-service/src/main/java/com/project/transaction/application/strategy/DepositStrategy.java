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
public class DepositStrategy implements TransactionStrategy {

    private final AccountGatewayPort accountGateway;

    @Override
    public void execute(Transaction transaction) {
        String destinationAccountId = transaction.destinationAccountId().value();
        BigDecimal amount = transaction.amount().amount();

        log.info("Executing deposit strategy for account: {} with amount: {}", destinationAccountId, amount);

        accountGateway.creditAccountBalance(amount, destinationAccountId);

        log.info("Deposit completed successfully for account: {}", destinationAccountId);
    }

    @Override
    public boolean supports(TransactionType type) {
        return TransactionType.DEPOSIT == type;
    }
}
