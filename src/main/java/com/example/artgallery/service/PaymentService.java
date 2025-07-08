package com.example.artgallery.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentService {
    
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;
    
    /**
     * Створити платіжний інтент в Stripe
     * @param amount сума платежу в центах
     * @param currency валюта (usd, eur, etc.)
     * @param description опис платежу
     * @return PaymentIntent об'єкт
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency, String description) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();
        
        return PaymentIntent.create(params);
    }
    
    /**
     * Підтвердити платіж
     * @param paymentIntentId ID платіжного інтенту
     * @return true якщо платіж успішний
     */
    public boolean confirmPayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            System.err.println("Error confirming payment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Отримати статус платежу
     * @param paymentIntentId ID платіжного інтенту
     * @return статус платежу
     */
    public String getPaymentStatus(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.getStatus();
        } catch (StripeException e) {
            System.err.println("Error getting payment status: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * Конвертувати BigDecimal в центи для Stripe
     * @param amount сума в доларах
     * @return сума в центах
     */
    public Long convertToCents(BigDecimal amount) {
        return amount.multiply(new BigDecimal("100")).longValue();
    }
} 