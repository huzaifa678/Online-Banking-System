package com.project.payment.infrastructure.external;

import com.project.payment.application.port.out.PaymentGatewayPort;
import com.project.payment.domain.exception.PaymentGatewayException;
import com.project.payment.domain.vo.Money;
import com.project.payment.domain.vo.PaymentStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adapter implementing {@link PaymentGatewayPort} by delegating to the Stripe API. It handles payment processing, including charging a payment method and managing idempotency keys. The class uses Resilience4j for circuit breaking and retry mechanisms to ensure robustness in case of failures.
 */
@Slf4j
@Component
public class StripePaymentGateway implements PaymentGatewayPort {

    private static final String RESILIENCE_INSTANCE = "paymentGateway";

    @Override
    @CircuitBreaker(name = RESILIENCE_INSTANCE, fallbackMethod = "chargeFallback")
    @Retry(name = RESILIENCE_INSTANCE)
    public PaymentStatus charge(Money amount, String paymentMethodId, String idempotencyKey) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount.amount().multiply(BigDecimal.valueOf(100)).longValue()) // Convert to cents
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .build();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(idempotencyKey)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params, options);

            return switch (paymentIntent.getStatus()) {
                case "succeeded" -> PaymentStatus.COMPLETED;
                case "processing", "requires_action" -> PaymentStatus.PENDING;
                default -> PaymentStatus.FAILED;
            };
        } catch (StripeException e) {
            log.error("Error during payment processing: {}", e.getMessage());
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Resilience4j fallback: invoked when the charge keeps failing (retries
     * exhausted) or the circuit is open. Signals gateway unavailability rather than
     * silently marking the payment as failed.
     */
    @SuppressWarnings("unused")
    private PaymentStatus chargeFallback(Money amount, String paymentMethodId, String idempotencyKey, Throwable t) {
        log.error("Payment gateway unavailable for idempotencyKey {}: {}", idempotencyKey, t.getMessage());
        throw new PaymentGatewayException("Payment gateway unavailable: " + t.getMessage(), t);
    }
}
