package com.srivath.payment.services;

import com.razorpay.RazorpayException;
import com.srivath.ecombasedomain.dtos.PaymentCompletedDto;
import com.srivath.ecombasedomain.events.Event;
import com.srivath.ecombasedomain.events.PaymentCompletedEvent;
import com.srivath.payment.clients.RazorpayGatewayClient;
import com.srivath.payment.dtos.PaymentDto;
import com.srivath.payment.models.Payment;
import com.srivath.payment.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    RazorpayGatewayClient razorpayGatewayClient;

    @Autowired
    KafkaTemplate<String, Event> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    public Payment getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
    }

    public Payment createPayment(PaymentDto dto) throws RazorpayException {
        //Create PaymentLink from RazorPay
        String paymentLink = razorpayGatewayClient.createPaymentLink(dto.getOrderId(), dto.getAmount(), dto.getCustomerName(), dto.getCustomerEmail(), dto.getCustomerPhone());
        Payment payment = new Payment();
        payment.setOrderId(dto.getOrderId());
        payment.setPaymentLink(paymentLink);
        payment.setAmount(dto.getAmount());
        payment.setUserName(dto.getCustomerName());
        payment.setUserEmail(dto.getCustomerEmail());
        payment.setUserPhone(dto.getCustomerPhone());
        payment.setStatus("PENDING");
        return paymentRepository.save(payment);
    }

    public void updatePaymentStatus(Long orderId, String razorpay_payment_link_status, String razorpay_payment_link_id, String razorpay_payment_id, String razorpay_payment_link_reference_id, String razorpay_signature)  {
        System.out.println("Payment status update for order ID: " + orderId + " with status: " + razorpay_payment_link_status);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new RuntimeException("No payment found for order ID: " + orderId);
        }
        Payment payment = payments.get(0);
        // Here you would typically call the Razorpay API to get the latest status
        // For demonstration, let's assume the status is updated to "COMPLETED"
        if (!razorpay_payment_link_status.equals("paid")) {
            payment.setStatus("COMPLETED");
        }
        else
        {
            payment.setStatus(razorpay_payment_link_status);
            payment.setRazorpayPaymentLinkStatus(razorpay_payment_link_status);
            payment.setRazorpayPaymentLinkId(razorpay_payment_link_id);
            payment.setRazorpayPaymentId(razorpay_payment_id);
            payment.setRazorpayPaymentLinkReferenceId(razorpay_payment_link_reference_id);
            payment.setRazorpaySignature(razorpay_signature);
        }

        // Save the updated payment status
        paymentRepository.save(payment);
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
        PaymentCompletedDto dto = new PaymentCompletedDto();
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setUserName(payment.getUserName());
        dto.setUserEmail(payment.getUserEmail());
        dto.setUserPhone(payment.getUserPhone());
        paymentCompletedEvent.setPaymentDto(dto);
        // Send the event to Kafka
        kafkaTemplate.send(topicName, paymentCompletedEvent);
    }


}
