package com.srivath.payment.controllers;

import com.srivath.payment.dtos.PaymentDto;
import com.srivath.payment.models.Payment;
import com.srivath.payment.repositories.PaymentRepository;
import com.srivath.payment.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public Payment getPayments(@PathVariable Long orderId){
        // Logic to retrieve payments for the given orderId
        // This will typically involve calling a service method that interacts with the repository
        return paymentService.getPaymentsByOrderId(orderId);
    }

    @PostMapping("/paymentLink")
    public Payment createPayment(@RequestBody PaymentDto paymentDto) {
        // Logic to create a new payment
        // This will typically involve calling a service method that interacts with the repository
        return paymentService.createPayment(paymentDto);
    }

    @GetMapping("/callback")
    public void updatePaymentStatus(@RequestParam Long orderId, @RequestParam String razorpay_payment_id, @RequestParam String razorpay_payment_link_id, @RequestParam String razorpay_payment_link_reference_id, @RequestParam String razorpay_payment_link_status, @RequestParam String razorpay_signature ) {
        // Logic to update the status of a payment
        paymentService.updatePaymentStatus(orderId, razorpay_payment_link_status );
    }
}
