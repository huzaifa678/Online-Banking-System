package com.project.payment.service;
import com.project.payment.client.AccountClient;
import com.project.payment.event.PaymentCreatedEvent;
import com.project.payment.model.Status;
import com.project.payment.model.document.Payment;
import com.project.payment.model.dto.CardDto;
import com.project.payment.model.dto.PaymentDto;
import com.project.payment.model.mapper.PaymentMapper;
import com.project.payment.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodCreateParams;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private AccountClient accountClient;

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    PaymentMethod cardDetails(CardDto cardDto) throws StripeException {
        PaymentMethodCreateParams createParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.CardDetails.builder()
                        .setNumber(cardDto.getCardNumber())
                        .setExpMonth(cardDto.getExpiryMonth())
                        .setExpYear(cardDto.getExpiryYear())
                        .setCvc(cardDto.getCvc())
                        .build())
                .build();

        PaymentMethod paymentMethod = PaymentMethod.create(createParams);
        return paymentMethod;
    }

    public PaymentDto createPayment(PaymentDto paymentDto, CardDto dto) throws StripeException {

        if (paymentDto == null) {
            throw new IllegalArgumentException("PaymentDto cannot be null");
        }

        System.out.println(paymentDto);

        if (!accountClient.doesAccountExists(paymentDto.getSource_accountId())) {
            throw new RuntimeException("Source Account with ID: " + paymentDto.getSource_accountId() + " does not exist");
        }

        if (accountClient.isAccountClosed(paymentDto.getSource_accountId())) {
            throw new RuntimeException("The source Account with ID: " + paymentDto.getSource_accountId() + " is closed");
        }

        if (!accountClient.accountBalance(paymentDto.getSource_accountId(), paymentDto.getAmount())) {
            throw new RuntimeException("Account does not have sufficient balance to proceed with the payment");
        }

        if (!accountClient.doesAccountExists(paymentDto.getDestination_accountId())) {
            throw new RuntimeException("Destination Account with ID: " + paymentDto.getDestination_accountId() + " does not exist");
        }

        if (accountClient.isAccountClosed(paymentDto.getDestination_accountId())) {
            throw new RuntimeException("The destination Account with ID: " + paymentDto.getDestination_accountId() + " is closed");
        }

        PaymentMethod paymentMethod = cardDetails(dto);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentDto.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // Amount in cents
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethod.getId())
                    .setConfirm(true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                paymentDto.setStatus(Status.COMPLETED);
            } else {
                paymentDto.setStatus(Status.FAILED);
            }

        } catch (StripeException e) {
            paymentDto.setStatus(Status.FAILED);
            e.printStackTrace();
        }
        accountClient.creditAccountBalance(paymentDto.getAmount(), paymentDto.getSource_accountId());
        accountClient.debitAccountBalance(paymentDto.getAmount(), paymentDto.getDestination_accountId());
        Payment payment = paymentMapper.ConvertToDocument(paymentDto);
        Payment savedPayment = paymentRepository.save(payment);
        String status = savedPayment.getStatus().toString();
        PaymentCreatedEvent paymentCreatedEvent = new PaymentCreatedEvent();
        paymentCreatedEvent.setStatus(status);
        log.info("Started sending PaymentCreatedEvent {} to kafka topic Payment-created", paymentCreatedEvent);
        kafkaTemplate.send("payment-created", paymentCreatedEvent);
        log.info("Ended sending PaymentCreatedEvent {} to kafka topic payment-created", paymentCreatedEvent);
        return paymentMapper.ConvertToDto(savedPayment);
    }

    public PaymentDto getPaymentById(String paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        try {
            if (paymentOptional.isPresent()) {
                return paymentMapper.ConvertToDto(paymentOptional.get());
            } else{
                throw new RuntimeException("Payment ID " + paymentId + " not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while finding the payment ID: " + paymentId + "Error occurred: " + e);
        }

    }

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::ConvertToDto)
                .collect(Collectors.toList());
    }
}
