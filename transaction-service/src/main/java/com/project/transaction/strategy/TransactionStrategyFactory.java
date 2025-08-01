package com.project.transaction.strategy;

import com.project.transaction.model.TransactionTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionStrategyFactory {
    
    private final List<TransactionStrategy> strategies;
    
    public TransactionStrategy getStrategy(TransactionTypes type) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for transaction type: " + type));
    }
} 