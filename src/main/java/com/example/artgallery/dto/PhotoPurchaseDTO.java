package com.example.artgallery.dto;

import com.example.artgallery.entity.PhotoPurchase;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PhotoPurchaseDTO {
    private Long id;
    private Long photoId;
    private String photoTitle;
    private String customerEmail;
    private BigDecimal amountPaid;
    private LocalDateTime purchaseDate;
    private String transactionId;
    private String downloadToken;
    private LocalDateTime tokenExpiry;
    private PhotoPurchase.PurchaseStatus status;
    
    // Додаткові поля для фронтенду
    private String downloadUrl;
    private boolean isExpired;
    private boolean canDownload;
    
    // Конструктори
    public PhotoPurchaseDTO() {}
    
    public PhotoPurchaseDTO(Long id, Long photoId, String photoTitle, String customerEmail, 
                           BigDecimal amountPaid, LocalDateTime purchaseDate, String transactionId,
                           String downloadToken, LocalDateTime tokenExpiry, PhotoPurchase.PurchaseStatus status,
                           String downloadUrl) {
        this.id = id;
        this.photoId = photoId;
        this.photoTitle = photoTitle;
        this.customerEmail = customerEmail;
        this.amountPaid = amountPaid;
        this.purchaseDate = purchaseDate;
        this.transactionId = transactionId;
        this.downloadToken = downloadToken;
        this.tokenExpiry = tokenExpiry;
        this.status = status;
        this.downloadUrl = downloadUrl;
    }
    
    // Геттери та сеттери
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }
    
    public String getPhotoTitle() { return photoTitle; }
    public void setPhotoTitle(String photoTitle) { this.photoTitle = photoTitle; }
    
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
    
    public PhotoPurchase.PurchaseStatus getStatus() { return status; }
    public void setStatus(PhotoPurchase.PurchaseStatus status) { this.status = status; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public boolean getIsExpired() {
        return tokenExpiry != null && tokenExpiry.isBefore(LocalDateTime.now());
    }
    
    public void setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }
    
    public boolean getCanDownload() {
        return status == PhotoPurchase.PurchaseStatus.COMPLETED && !getIsExpired();
    }
    
    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }
} 