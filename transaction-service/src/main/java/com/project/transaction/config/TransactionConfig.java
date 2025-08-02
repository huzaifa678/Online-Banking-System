package com.project.transaction.config;

import com.project.transaction.strategy.TransactionStrategy;
import com.project.transaction.strategy.TransactionStrategyFactory;
import com.project.transaction.validator.SourceAccountValidator;
import com.project.transaction.validator.TransactionAmountValidator;
import com.project.transaction.validator.TransactionValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TransactionConfig {
    
    @Bean
    public TransactionValidator validationChain(TransactionAmountValidator transactionAmountValidator, SourceAccountValidator sourceAccountValidator) {
        transactionAmountValidator.setNext(sourceAccountValidator);
        return transactionAmountValidator;
    }
    
    @Bean
    public TransactionStrategyFactory transactionStrategyFactory(List<TransactionStrategy> strategies) {
        return new TransactionStrategyFactory(strategies);
    }
} 