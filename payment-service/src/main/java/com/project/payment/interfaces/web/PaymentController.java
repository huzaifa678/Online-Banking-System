package com.project.payment.interfaces.web;

import com.project.payment.application.dto.PaymentCardDto;
import com.project.payment.application.dto.PaymentDto;
import com.project.payment.application.port.in.CreatePaymentUseCase;
import com.project.payment.application.port.in.GetPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody PaymentCardDto paymentCardDto) {
        try {
            PaymentDto createdPayment = createPaymentUseCase.createPayment(paymentCardDto);
            return ResponseEntity.ok(createdPayment.getPaymentId());
        } catch (Exception e) {
            System.out.println("Error occurred:");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable String paymentId) {
        try {
            PaymentDto payment = getPaymentUseCase.getPaymentById(paymentId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            System.out.println("Error occurred:");
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        try {
            List<PaymentDto> payments = getPaymentUseCase.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
