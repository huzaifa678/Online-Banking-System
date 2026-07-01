package com.project.payment.application.service;

import com.project.payment.application.dto.PaymentCardDto;
import com.project.payment.application.dto.PaymentDto;
import com.project.payment.application.mapper.PaymentDtoMapper;
import com.project.payment.application.port.in.CreatePaymentUseCase;
import com.project.payment.application.port.in.GetPaymentUseCase;
import com.project.payment.application.port.out.AccountGatewayPort;
import com.project.payment.application.port.out.PaymentEventPublisherPort;
import com.project.payment.application.port.out.PaymentGatewayPort;
import com.project.payment.application.port.out.PaymentRepositoryPort;
import com.project.payment.domain.exception.PaymentValidationException;
import com.project.payment.domain.model.Payment;
import com.project.payment.domain.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentApplicationService implements CreatePaymentUseCase, GetPaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final AccountGatewayPort accountGateway;
    private final PaymentGatewayPort paymentGateway;
    private final PaymentEventPublisherPort eventPublisher;

    @Override
    @CacheEvict(value = "payments", allEntries = true)
    public PaymentDto createPayment(PaymentCardDto paymentCardDto) {
        if (paymentCardDto == null || paymentCardDto.getPaymentDto() == null) {
            throw new IllegalArgumentException("PaymentCardDto cannot be null");
        }

        PaymentDto paymentDto = paymentCardDto.getPaymentDto();
        String paymentMethodId = paymentCardDto.getPaymentMethodId();

        validateAccounts(paymentDto);

        Payment payment = PaymentDtoMapper.toNewPayment(paymentDto);

        // Stable per-request key so resilience retries of the charge don't double-charge.
        String idempotencyKey = UUID.randomUUID().toString();
        PaymentStatus status = paymentGateway.charge(payment.amount(), paymentMethodId, idempotencyKey);
        applyStatus(payment, status);

        if (payment.isCompleted()) {
            processAccountBalances(payment);
        }

        Payment saved = paymentRepository.save(payment);

        eventPublisher.publishAll(payment.domainEvents());
        payment.clearEvents();

        return PaymentDtoMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "payments", key = "#id", unless = "#result == null")
    public PaymentDto getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(PaymentDtoMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    @Override
    @Cacheable(value = "allPayments")
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentDtoMapper::toDto)
                .toList();
    }

    private void validateAccounts(PaymentDto paymentDto) {
        String sourceAccountId = paymentDto.getSource_accountId();
        String destinationAccountId = paymentDto.getDestination_accountId();

        if (!accountGateway.doesAccountExists(sourceAccountId)) {
            throw new PaymentValidationException("Source Account with ID: " + sourceAccountId + " does not exist");
        }

        if (accountGateway.isAccountClosed(sourceAccountId)) {
            throw new PaymentValidationException("The source account with ID: " + sourceAccountId + " is closed");
        }

        if (!accountGateway.accountBalance(sourceAccountId, paymentDto.getAmount())) {
            throw new PaymentValidationException("Insufficient balance in the source account");
        }

        if (!accountGateway.doesAccountExists(destinationAccountId)) {
            throw new PaymentValidationException("Destination Account with ID: " + destinationAccountId + " does not exist");
        }

        if (accountGateway.isAccountClosed(destinationAccountId)) {
            throw new PaymentValidationException("The destination account with ID: " + destinationAccountId + " is closed");
        }
    }

    private void applyStatus(Payment payment, PaymentStatus status) {
        switch (status) {
            case COMPLETED -> payment.complete();
            case PENDING -> payment.pending();
            case FAILED -> payment.fail();
        }
    }

    private void processAccountBalances(Payment payment) {
        java.math.BigDecimal amount = payment.amount().amount();
        accountGateway.creditAccountBalance(amount, payment.sourceAccountId().value());
        accountGateway.debitAccountBalance(amount, payment.destinationAccountId().value());
    }
}
