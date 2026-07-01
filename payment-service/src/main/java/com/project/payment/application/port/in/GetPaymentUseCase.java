package com.project.payment.application.port.in;

import com.project.payment.application.dto.PaymentDto;

import java.util.List;


public interface GetPaymentUseCase {

    PaymentDto getPaymentById(String id);

    List<PaymentDto> getAllPayments();
}
