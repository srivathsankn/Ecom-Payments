package com.srivath.payment.repositories;

import com.srivath.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderId(Long orderId);

    // Custom query methods can be defined here if needed
    // For example, to find payments by orderId:
    // List<Payments> findByOrderId(Long orderId);
}
