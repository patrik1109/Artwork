package com.example.artgallery.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "photo_purchases")
public class PhotoPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;
    
    private String customerEmail;
    private BigDecimal amountPaid;
    private LocalDateTime purchaseDate;
    private String transactionId;
    private String downloadToken; // Токен для безпечного скачування
    private LocalDateTime tokenExpiry; // Термін дії токена
    
    // Статус покупки
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;
    
    public enum PurchaseStatus {
        PENDING, COMPLETED, FAILED, EXPIRED
    }
    
    // Конструктори
    public PhotoPurchase() {}
    
    public PhotoPurchase(Photo photo, String customerEmail, BigDecimal amountPaid, String transactionId) {
        this.photo = photo;
        this.customerEmail = customerEmail;
        this.amountPaid = amountPaid;
        this.transactionId = transactionId;
        this.purchaseDate = LocalDateTime.now();
        this.status = PurchaseStatus.PENDING;
    }
    
    // Геттери та сеттери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Photo getPhoto() { return photo; }
    public void setPhoto(Photo photo) { this.photo = photo; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getDownloadToken() { return downloadToken; }
    public void setDownloadToken(String downloadToken) { this.downloadToken = downloadToken; }
    
    public LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }
    
    public PurchaseStatus getStatus() { return status; }
    public void setStatus(PurchaseStatus status) { this.status = status; }
} 