package com.example.artgallery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class StripePaymentRequestDTO {
    
    @NotNull(message = "Photo ID is required")
    private Long photoId;
    
    @NotNull(message = "Customer email is required")
    private String customerEmail;
    
    @NotNull(message = "Payment intent ID is required")
    private String paymentIntentId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Currency is required")
    private String currency = "usd";
    
    // Конструктори
    public StripePaymentRequestDTO() {}
    
    public StripePaymentRequestDTO(Long photoId, String customerEmail, String paymentIntentId, Double amount, String currency) {
        this.photoId = photoId;
        this.customerEmail = customerEmail;
        this.paymentIntentId = paymentIntentId;
        this.amount = amount;
        this.currency = currency;
    }
    
    // Геттери та сеттери
    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
} 