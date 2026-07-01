package com.project.payment.application.port.out;

import com.project.payment.domain.vo.Money;
import com.project.payment.domain.vo.PaymentStatus;


public interface PaymentGatewayPort {

    PaymentStatus charge(Money amount, String paymentMethodId, String idempotencyKey);
}
