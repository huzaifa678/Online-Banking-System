package com.project.payment.controller;


import com.project.payment.model.dto.CardDto;
import com.project.payment.model.dto.PaymentCardDto;
import com.project.payment.model.dto.PaymentDto;
import com.project.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody PaymentCardDto paymentCardDto) {
        try {
            PaymentDto paymentDto = paymentCardDto.getPaymentDto();
            CardDto cardDto = paymentCardDto.getCardDto();
            PaymentDto newPaymentDto = paymentService.createPayment(paymentDto, cardDto);
            System.out.println("payment created");
            return ResponseEntity.status(HttpStatus.CREATED).body(newPaymentDto.getPaymentId());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the payment: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable String id) {
        try {
            PaymentDto paymentDto = paymentService.getPaymentById(id);
            return ResponseEntity.ok(paymentDto);
        } catch (Exception e) {
            throw new RuntimeException("Could not find the payment with ID " + id + ": " + e.getMessage(), e);
        }
    }

}
