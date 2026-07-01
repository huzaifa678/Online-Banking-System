package com.project.transaction.application.strategy;

import com.project.transaction.domain.vo.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class TransactionStrategyFactory {

    private final List<TransactionStrategy> strategies;

    public TransactionStrategy getStrategy(TransactionType type) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for transaction type: " + type));
    }
}
