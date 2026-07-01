package com.project.payment.application.port.in;

import com.project.payment.application.dto.PaymentCardDto;
import com.project.payment.application.dto.PaymentDto;


public interface CreatePaymentUseCase {

    PaymentDto createPayment(PaymentCardDto paymentCardDto);
}
