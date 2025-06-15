package com.srivath.payment.listeners;

import com.razorpay.RazorpayException;
import com.srivath.ecombasedomain.events.Event;
import com.srivath.ecombasedomain.events.OrderPlacedEvent;
import com.srivath.ecombasedomain.events.PaymentLinkCreatedEvent;
import com.srivath.ecombasedomain.events.PaymentLinkCreationFailedEvent;
import com.srivath.payment.dtos.PaymentDto;
import com.srivath.payment.models.Payment;
import com.srivath.payment.services.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventListeners {

    @Autowired
    PaymentService paymentService;

    @Autowired
    KafkaTemplate<String, Event> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Event event)  {
        // Process the incoming message
        if (event != null && "ORDER_PLACED".equals(event.getEventName()))
        {
            OrderPlacedEvent orderPlacedEvent = (OrderPlacedEvent) event;
            PaymentDto dto = new PaymentDto();
            dto.setOrderId(orderPlacedEvent.getOrderDto().getOrderId());
            dto.setCustomerName(orderPlacedEvent.getOrderDto().getUserName());
            dto.setCustomerEmail(orderPlacedEvent.getOrderDto().getUserEmail());
            dto.setCustomerPhone(orderPlacedEvent.getOrderDto().getUserPhone());
            dto.setAmount(orderPlacedEvent.getOrderDto().getOrderAmount());

            try {
                Payment payment = paymentService.createPayment(dto);
                PaymentLinkCreatedEvent paymentLinkCreatedEvent = new PaymentLinkCreatedEvent();
                paymentLinkCreatedEvent.setOrderId(payment.getOrderId());
                paymentLinkCreatedEvent.setUserName(dto.getCustomerName());
                paymentLinkCreatedEvent.setUserEmail(dto.getCustomerEmail());
                paymentLinkCreatedEvent.setUserPhone(dto.getCustomerPhone());
                paymentLinkCreatedEvent.setAmount(payment.getAmount());
                paymentLinkCreatedEvent.setPaymentLink(payment.getPaymentLink());
                // Send PaymentLinkCreatedEvent to Kafka
                kafkaTemplate.send(topicName, paymentLinkCreatedEvent);
            }
            catch (RazorpayException e) {
                //Payment Failed event to be sent to Kafka. OrderService will handle it and mark order as FAILED. Notification will be sent to user.
                PaymentLinkCreationFailedEvent paymentLinkCreationFailedEvent = new PaymentLinkCreationFailedEvent();
                paymentLinkCreationFailedEvent.setOrderId(dto.getOrderId());
                paymentLinkCreationFailedEvent.setUserName(dto.getCustomerName());
                paymentLinkCreationFailedEvent.setUserEmail(dto.getCustomerEmail());
                paymentLinkCreationFailedEvent.setUserPhone(dto.getCustomerPhone());
                paymentLinkCreationFailedEvent.setAmount(dto.getAmount());
                paymentLinkCreationFailedEvent.setErrorMessage(e.getMessage());
                // Send PaymentLinkCreationFailedEvent to Kafka
                kafkaTemplate.send(topicName, paymentLinkCreationFailedEvent);
            }

        }
    }
}
