package com.project.payment.model.mapper;

import com.project.payment.model.document.Payment;
import com.project.payment.model.dto.PaymentDto;
import org.springframework.stereotype.Component;


@Component
public class PaymentMapper extends BaseMapper<Payment, PaymentDto>{

    @Override
    public Payment ConvertToDocument(PaymentDto dto, Object... args) {
        return Payment.builder()
                .paymentId(dto.getPaymentId())
                .source_accountId(dto.getSource_accountId())
                .destination_accountId(dto.getDestination_accountId())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .paymentmethod(dto.getPaymentmethod())
                .build();
    }

    @Override
    public PaymentDto ConvertToDto(Payment document, Object... args) {
        return PaymentDto.builder()
                .paymentId(document.getPaymentId())
                .source_accountId(document.getSource_accountId())
                .destination_accountId(document.getDestination_accountId())
                .amount(document.getAmount())
                .status(document.getStatus())
                .paymentmethod(document.getPaymentmethod())
                .build();
    }
}
