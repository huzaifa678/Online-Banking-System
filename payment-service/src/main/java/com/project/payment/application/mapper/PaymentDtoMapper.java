package com.project.payment.application.mapper;

import com.project.payment.application.dto.PaymentDto;
import com.project.payment.domain.model.Payment;
import com.project.payment.domain.vo.AccountId;
import com.project.payment.domain.vo.Money;


public final class PaymentDtoMapper {

    private PaymentDtoMapper() {
    }

    public static PaymentDto toDto(Payment payment) {
        return PaymentDto.builder()
                .paymentId(payment.paymentId() == null ? null : payment.paymentId().value())
                .source_accountId(payment.sourceAccountId() == null ? null : payment.sourceAccountId().value())
                .destination_accountId(payment.destinationAccountId() == null ? null : payment.destinationAccountId().value())
                .amount(payment.amount().amount())
                .status(payment.status())
                .paymentmethod(payment.method())
                .build();
    }

    /**
     * Start a new {@link Payment} aggregate from an incoming payment DTO.
     */
    public static Payment toNewPayment(PaymentDto dto) {
        return Payment.initiate(
                AccountId.ofNullable(dto.getSource_accountId()),
                AccountId.ofNullable(dto.getDestination_accountId()),
                Money.of(dto.getAmount()),
                dto.getPaymentmethod());
    }
}
