package com.example.artgallery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class PurchaseRequestDTO {
    @NotNull(message = "Photo ID is required")
    private Long photoId;
    
    @NotNull(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    // Додаткові поля для платіжної інформації
    private String paymentMethod; // "card", "paypal", etc.
    private String currency = "USD"; // За замовчуванням USD
    
    // Конструктори
    public PurchaseRequestDTO() {}
    
    public PurchaseRequestDTO(Long photoId, String customerEmail, String paymentMethod, String currency) {
        this.photoId = photoId;
        this.customerEmail = customerEmail;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
    }
    
    // Геттери та сеттери
    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
} 