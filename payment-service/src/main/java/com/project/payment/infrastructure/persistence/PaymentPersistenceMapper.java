package com.project.payment.infrastructure.persistence;

import com.project.payment.domain.model.Payment;
import com.project.payment.domain.vo.AccountId;
import com.project.payment.domain.vo.Money;
import com.project.payment.domain.vo.PaymentId;

import java.math.BigDecimal;


final class PaymentPersistenceMapper {

    private PaymentPersistenceMapper() {
    }

    static Payment toDomain(PaymentDocument document) {
        BigDecimal amount = document.getAmount() == null ? BigDecimal.ZERO : document.getAmount();
        return Payment.reconstitute(
                PaymentId.ofNullable(document.getPaymentId()),
                AccountId.ofNullable(document.getSource_accountId()),
                AccountId.ofNullable(document.getDestination_accountId()),
                Money.of(amount),
                document.getPaymentmethod(),
                document.getStatus());
    }

    static PaymentDocument toDocument(Payment payment) {
        return PaymentDocument.builder()
                .paymentId(payment.paymentId() == null ? null : payment.paymentId().value())
                .source_accountId(payment.sourceAccountId() == null ? null : payment.sourceAccountId().value())
                .destination_accountId(payment.destinationAccountId() == null ? null : payment.destinationAccountId().value())
                .amount(payment.amount().amount())
                .status(payment.status())
                .paymentmethod(payment.method())
                .build();
    }
}
