package com.project.payment.controller;

import com.project.payment.model.dto.PaymentCardDto;
import com.project.payment.model.dto.PaymentDto;
import com.project.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody PaymentCardDto paymentCardDto) {
        try {
            PaymentDto createdPayment = paymentService.createPayment(paymentCardDto);
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
            PaymentDto payment = paymentService.getPaymentById(paymentId);
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
            List<PaymentDto> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
