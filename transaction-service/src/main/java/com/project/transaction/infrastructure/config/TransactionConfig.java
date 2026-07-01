package com.project.transaction.infrastructure.config;

import com.project.transaction.application.strategy.TransactionStrategy;
import com.project.transaction.application.strategy.TransactionStrategyFactory;
import com.project.transaction.application.validation.SourceAccountValidator;
import com.project.transaction.application.validation.TransactionAmountValidator;
import com.project.transaction.application.validation.TransactionValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Wires the transaction validation Chain of Responsibility (amount → source) and
 * the strategy factory. The chain composition is unchanged from the previous
 * {@code config.TransactionConfig}.
 */
@Configuration
public class TransactionConfig {

    @Bean
    public TransactionValidator validationChain(TransactionAmountValidator transactionAmountValidator,
                                                SourceAccountValidator sourceAccountValidator) {
        transactionAmountValidator.setNext(sourceAccountValidator);
        return transactionAmountValidator;
    }

    @Bean
    public TransactionStrategyFactory transactionStrategyFactory(List<TransactionStrategy> strategies) {
        return new TransactionStrategyFactory(strategies);
    }
}
