package com.example.artgallery.dto;

import com.example.artgallery.entity.VideoPurchase;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VideoPurchaseDTO {
    private Long id;
    private Long videoId;
    private String videoTitle;
    private String customerEmail;
    private BigDecimal amountPaid;
    private LocalDateTime purchaseDate;
    private String transactionId;
    private String downloadToken;
    private LocalDateTime tokenExpiry;
    private VideoPurchase.PurchaseStatus status;
    private String downloadUrl;
    private boolean isExpired;
    private boolean canDownload;

    public VideoPurchaseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getVideoTitle() { return videoTitle; }
    public void setVideoTitle(String videoTitle) { this.videoTitle = videoTitle; }

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

    public VideoPurchase.PurchaseStatus getStatus() { return status; }
    public void setStatus(VideoPurchase.PurchaseStatus status) { this.status = status; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public boolean getIsExpired() {
        return tokenExpiry != null && tokenExpiry.isBefore(LocalDateTime.now());
    }

    public void setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public boolean getCanDownload() {
        return status == VideoPurchase.PurchaseStatus.COMPLETED && !getIsExpired();
    }

    public void setCanDownload(boolean canDownload) {
        this.canDownload = canDownload;
    }
}
