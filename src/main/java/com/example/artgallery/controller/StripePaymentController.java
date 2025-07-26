package com.example.artgallery.controller;

import com.example.artgallery.dto.StripePaymentRequestDTO;
import com.example.artgallery.service.PaymentService;
import com.example.artgallery.service.PhotoPurchaseService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = {"http://localhost:3000", "https://artwork-production-fec7.up.railway.app"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class StripePaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PhotoPurchaseService photoPurchaseService;
    
    @Value("${stripe.publishable-key}")
    private String publishableKey;
    
    /**
     * Створити платіжний інтент
     */
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, Object>> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            Double amount = Double.parseDouble(request.get("amount").toString());
            String currency = (String) request.getOrDefault("currency", "usd");
            String description = (String) request.getOrDefault("description", "Photo purchase");
            String customerEmail = (String) request.get("customerEmail");
            Long photoId = Long.parseLong(request.get("photoId").toString());
            
            // Дозволяємо багаторазові покупки того самого фото
            
            // Конвертувати в центи
            Long amountInCents = paymentService.convertToCents(BigDecimal.valueOf(amount));
            
            // Створити платіжний інтент
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(amountInCents, currency, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("paymentIntentId", paymentIntent.getId());
            
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid request data");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Підтвердити платіж та створити покупку
     */
    @PostMapping("/confirm-payment")
    public ResponseEntity<Map<String, Object>> confirmPayment(@Valid @RequestBody StripePaymentRequestDTO request) {
        try {
            // Підтвердити платіж
            boolean paymentConfirmed = paymentService.confirmPayment(request.getPaymentIntentId());
            
            if (!paymentConfirmed) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Payment not confirmed");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Створити покупку
            var purchaseRequest = new com.example.artgallery.dto.PurchaseRequestDTO();
            purchaseRequest.setPhotoId(request.getPhotoId());
            purchaseRequest.setCustomerEmail(request.getCustomerEmail());
            purchaseRequest.setPaymentMethod("stripe");
            purchaseRequest.setCurrency(request.getCurrency());
            
            var purchase = photoPurchaseService.createPurchase(purchaseRequest);
            // Confirm purchase (set status COMPLETED and generate downloadToken)
            purchase = photoPurchaseService.confirmPayment(purchase.getTransactionId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("purchase", purchase);
            response.put("message", "Payment confirmed and purchase created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Отримати публічний ключ Stripe
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publishableKey", publishableKey);
        return ResponseEntity.ok(config);
    }
    
    /**
     * Перевірити статус платежу
     */
    @GetMapping("/payment-status/{paymentIntentId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable String paymentIntentId) {
        try {
            String status = paymentService.getPaymentStatus(paymentIntentId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 