package com.project.payment.infrastructure.persistence;

import com.project.payment.application.port.out.PaymentRepositoryPort;
import com.project.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentDocument saved = paymentRepository.save(PaymentPersistenceMapper.toDocument(payment));
        return PaymentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return paymentRepository.findById(id).map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll().stream()
                .map(PaymentPersistenceMapper::toDomain)
                .toList();
    }
}
