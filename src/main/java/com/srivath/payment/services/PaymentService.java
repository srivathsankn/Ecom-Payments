package com.srivath.payment.services;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.srivath.ecombasedomain.dtos.PaymentCompletedDto;
import com.srivath.ecombasedomain.dtos.PaymentInstance;
import com.srivath.ecombasedomain.events.Event;
import com.srivath.ecombasedomain.events.PaymentCompletedEvent;
import com.srivath.payment.clients.RazorpayGatewayClient;
import com.srivath.payment.dtos.PaymentDto;
import com.srivath.payment.models.Payment;
import com.srivath.payment.repositories.PaymentRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.srivath.payment.utils.DateFormatter;

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

    Logger logger = org.slf4j.LoggerFactory.getLogger(PaymentService.class);

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

    public void updatePaymentStatus(Long orderId, String razorpay_payment_link_status, String razorpay_payment_link_id, String razorpay_payment_id, String razorpay_payment_link_reference_id, String razorpay_signature) throws RazorpayException {
        System.out.println("Payment status update for order ID: " + orderId + " with status: " + razorpay_payment_link_status);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            logger.error("No payment found for order ID: {}", orderId);
        }

        Payment payment = payments.get(payments.size()-1);
        //System.out.println("Latest payment record: " + payment);
        // Here you would typically call the Razorpay API to get the latest status
        // For demonstration, let's assume the status is updated to "COMPLETED"
        if (payment.getRazorpayPaymentId() != null)
        {
            // Partial payment has happened before. So Need to create new row.
            // If the paymentID does not exist, we create a new record
            //payment.setPaymentId(null); // Clear paymentId to insert new record
            Payment payment1 = new Payment(payment);
            payment = payment1;
        }

        payment.setRazorpayPaymentLinkStatus(razorpay_payment_link_status);
        payment.setRazorpayPaymentLinkId(razorpay_payment_link_id);
        payment.setRazorpayPaymentId(razorpay_payment_id);
        payment.setRazorpayPaymentLinkReferenceId(razorpay_payment_link_reference_id);
        payment.setRazorpaySignature(razorpay_signature);

        //Get Payment details from RazorPay
        com.razorpay.Payment razorpayPayment = razorpayGatewayClient.getPaymentDetails(razorpay_payment_id);
        Double paidAmount =  (Double) (Double.valueOf(razorpayPayment.get("amount") != null ? razorpayPayment.get("amount").toString() : "0.00") / 100.0); // Convert from paise to rupees
        payment.setPaidAmount(paidAmount);
        payment.setPartPaymentMethod(razorpayPayment.get("method") != null ? razorpayPayment.get("method").toString() : "unknown");
//        System.out.println("Payment details retrieved from Razorpay: " + razorpayPayment);
//        System.out.println("razorpayPayment.get(\"created_at\"): " + razorpayPayment.get("created_at"));
        String paymentDate = DateFormatter.convertDateToYYYYMMDDFormat(razorpayPayment.get("created_at"));
        payment.setPaymentDate(paymentDate);
        String paymentMethod = razorpayPayment.get("method");
        payment.setPartPaymentMethod(paymentMethod);

        switch (paymentMethod) {
            case "card":
                payment.setPaymentAdditionalInfo("Card Used");
                break;
            case "netbanking":
                payment.setPaymentAdditionalInfo("Bank : "+ razorpayPayment.get("bank"));
                break;
            case "upi":
                payment.setPaymentAdditionalInfo("UPI Id : " + razorpayPayment.get("upi.vpa"));
                break;
            case "wallet":
                payment.setPaymentAdditionalInfo("Wallet : " + razorpayPayment.get("wallet"));
                break;
            default:
                payment.setPaymentAdditionalInfo("Other");
        }

        if (razorpay_payment_link_status.equals("paid"))
            payment.setStatus("COMPLETED");
        else if (razorpay_payment_link_status.equals("partially_paid"))
        {
            payment.setStatus("PARTIALLY_COMPLETED");
        }

        //System.out.println("Payment record to be persisted: " + payment);
        // Save the updated payment status
        paymentRepository.save(payment);

        // Create and populate the PaymentCompletedEvent
        payments = paymentRepository.findByOrderId(orderId);

        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
        PaymentCompletedDto dto = new PaymentCompletedDto();
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setUserName(payment.getUserName());
        dto.setUserEmail(payment.getUserEmail());
        dto.setUserPhone(payment.getUserPhone());
        dto.setStatus(payment.getStatus());
        for (Payment p : payments) {
            PaymentInstance instance = new PaymentInstance();
            instance.setPaymentDate(p.getPaymentDate());
            instance.setPaymentMethod(p.getPartPaymentMethod());
            instance.setAmount(p.getPaidAmount());
            instance.setAdditionalInfo(p.getPaymentAdditionalInfo());
            instance.setPaymentStatus(p.getStatus());
            dto.getPaymentInstances().add(instance);
        }
        dto.setStatus(payments.get(payments.size()-1).getStatus());
        paymentCompletedEvent.setPaymentDto(dto);
        // Send the event to Kafka
        kafkaTemplate.send(topicName, paymentCompletedEvent);
    }


}
