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
public class TransferStrategy implements TransactionStrategy {

    private final AccountGatewayPort accountGateway;

    @Override
    public void execute(Transaction transaction) {
        String sourceAccountId = transaction.sourceAccountId().value();
        String destinationAccountId = transaction.destinationAccountId().value();
        BigDecimal amount = transaction.amount().amount();

        log.info("Executing transfer strategy from account: {} to account: {} with amount: {}",
                sourceAccountId, destinationAccountId, amount);

        accountGateway.debitAccountBalance(amount, sourceAccountId);
        accountGateway.creditAccountBalance(amount, destinationAccountId);

        log.info("Transfer completed successfully from account: {} to account: {}",
                sourceAccountId, destinationAccountId);
    }

    @Override
    public boolean supports(TransactionType type) {
        return TransactionType.TRANSFER == type;
    }
}
