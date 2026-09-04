package com.example.artgallery.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_purchases")
public class VideoPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private String customerEmail;
    private BigDecimal amountPaid;
    private LocalDateTime purchaseDate;
    private String transactionId;
    private String downloadToken;
    private LocalDateTime tokenExpiry;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    public enum PurchaseStatus {
        PENDING, COMPLETED, FAILED, EXPIRED
    }

    public VideoPurchase() {}

    public VideoPurchase(Video video, String customerEmail, BigDecimal amountPaid, String transactionId) {
        this.video = video;
        this.customerEmail = customerEmail;
        this.amountPaid = amountPaid;
        this.transactionId = transactionId;
        this.purchaseDate = LocalDateTime.now();
        this.status = PurchaseStatus.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

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
