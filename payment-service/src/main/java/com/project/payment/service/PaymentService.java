package com.project.payment.service;

import com.project.payment.client.AccountClient;
import com.project.payment.event.PaymentCreatedEvent;
import com.project.payment.model.Status;
import com.project.payment.model.document.Payment;
import com.project.payment.model.dto.PaymentDto;
import com.project.payment.model.dto.PaymentCardDto;
import com.project.payment.model.mapper.PaymentMapper;
import com.project.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private AccountClient accountClient;

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @CacheEvict(value = "payments", allEntries = true)
    public PaymentDto createPayment(PaymentCardDto paymentCardDto) {
        if (paymentCardDto == null || paymentCardDto.getPaymentDto() == null) {
            throw new IllegalArgumentException("PaymentCardDto cannot be null");
        }

        PaymentDto paymentDto = paymentCardDto.getPaymentDto();
        String paymentMethodId = paymentCardDto.getPaymentMethodId();

        validateAccounts(paymentDto);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentDto.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // Convert to cents
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            handlePaymentStatus(paymentDto, paymentIntent);

            if (paymentDto.getStatus() == Status.COMPLETED) {
                processAccountBalances(paymentDto);
            }

            Payment payment = paymentMapper.ConvertToDocument(paymentDto);
            Payment savedPayment = paymentRepository.save(payment);

            publishPaymentEvent(savedPayment);

            return paymentMapper.ConvertToDto(savedPayment);

        } catch (StripeException e) {
            log.error("Error during payment processing: {}", e.getMessage());
            paymentDto.setStatus(Status.FAILED);
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }

    private void validateAccounts(PaymentDto paymentDto) {
        String sourceAccountId = paymentDto.getSource_accountId();
        String destinationAccountId = paymentDto.getDestination_accountId();

        if (!accountClient.doesAccountExists(sourceAccountId)) {
            throw new RuntimeException("Source Account with ID: " + sourceAccountId + " does not exist");
        }

        if (accountClient.isAccountClosed(sourceAccountId)) {
            throw new RuntimeException("The source account with ID: " + sourceAccountId + " is closed");
        }

        if (!accountClient.accountBalance(sourceAccountId, paymentDto.getAmount())) {
            throw new RuntimeException("Insufficient balance in the source account");
        }

        if (!accountClient.doesAccountExists(destinationAccountId)) {
            throw new RuntimeException("Destination Account with ID: " + destinationAccountId + " does not exist");
        }

        if (accountClient.isAccountClosed(destinationAccountId)) {
            throw new RuntimeException("The destination account with ID: " + destinationAccountId + " is closed");
        }
    }

    private void handlePaymentStatus(PaymentDto paymentDto, PaymentIntent paymentIntent) {
        switch (paymentIntent.getStatus()) {
            case "succeeded":
                paymentDto.setStatus(Status.COMPLETED);
                break;
            case "processing":
            case "requires_action":
                paymentDto.setStatus(Status.PENDING);
                break;
            default:
                paymentDto.setStatus(Status.FAILED);
                break;
        }
    }

    private void processAccountBalances(PaymentDto paymentDto) {
        BigDecimal amount = paymentDto.getAmount();
        String sourceAccountId = paymentDto.getSource_accountId();
        String destinationAccountId = paymentDto.getDestination_accountId();

        accountClient.creditAccountBalance(amount, sourceAccountId);
        accountClient.debitAccountBalance(amount, destinationAccountId);
    }

    private void publishPaymentEvent(Payment savedPayment) {
        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent();
        paymentCreatedEvent.setStatus(savedPayment.getStatus().toString());
        log.info("Publishing PaymentCreatedEvent to Kafka topic 'payment-created': {}", paymentCreatedEvent);
        kafkaTemplate.send("payment-created", paymentCreatedEvent);
    }

    @Cacheable(value = "payments", key = "#id", unless = "#result == null")
    public PaymentDto getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::ConvertToDto)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    @Cacheable(value = "allPayments")
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::ConvertToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"payments", "allPayments"}, allEntries = true)
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }
}
