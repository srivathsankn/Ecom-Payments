package com.srivath.payment.configurations;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key}")
    public String razorpayKey;

    @Value("${razorpay.secret}")
    public String razorpaySecret;

    @Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(razorpayKey, razorpaySecret);
    }
}
