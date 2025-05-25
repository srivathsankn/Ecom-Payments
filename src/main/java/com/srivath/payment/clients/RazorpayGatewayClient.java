package com.srivath.payment.clients;

import com.razorpay.PaymentLink;
import org.json.JSONObject;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RazorpayGatewayClient {

    @Value("${razorpay.key}")
    public String razorpayKey;

    @Value("${razorpay.secret}")
    public String razorpaySecret;

    @Value("${razorpay.callback.url}")
    public String razorpayCallbackUrl;

    @Autowired
    RazorpayClient razorpayClient;

//    public RazorpayGatewayClient() {
//        try {
//            // Initialize Razorpay client with your API key and secret
//            razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
//        }
//    catch (RazorpayException e) {
//            e.printStackTrace();
//        }
//    }

    public String createPaymentLink(Long orderId, Double amount, String customerName, String customerEmail, String customerPhone) {

        try {
            // Create a new payment object
            //System.out.println(razorpayClient);
            if (razorpayClient == null) {
                System.out.println("Razorpay client is not initialized, initializing now...");
                razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
            }
            JSONObject paymentRequest = new JSONObject();
            paymentRequest.put("amount", amount * 100); // Amount in paise
            paymentRequest.put("currency", "INR");
            paymentRequest.put("accept_partial",true);
            paymentRequest.put("expire_by",System.currentTimeMillis()/1000 + 36000); // Link expires in 10 hour
            paymentRequest.put("reference_id", orderId.toString());
            paymentRequest.put("description", "Payment for Order ID: " + orderId);
//            paymentRequest.put("receipt", orderId.toString());

            JSONObject customer = new JSONObject();
            customer.put("name",customerName);
            customer.put("contact",customerPhone);
            customer.put("email",customerEmail);
            paymentRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("sms",true);
            notify.put("email",true);
            paymentRequest.put("notify",notify);

            paymentRequest.put("reminder_enable",true);
            JSONObject notes = new JSONObject();
            notes.put("policy_name","Ecommerce Order Payment");
            paymentRequest.put("notes",notes);
            paymentRequest.put("callback_url",razorpayCallbackUrl+"?orderId="+orderId);
            paymentRequest.put("callback_method","get");

            // Create the payment link
            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentRequest);
            return paymentLink.get("short_url").toString(); // Return the payment link

        } catch (RazorpayException e) {
            e.printStackTrace();
            return null; // Handle exception appropriately
        }


    }
}
