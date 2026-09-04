package com.example.artgallery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VideoStripePaymentRequestDTO {

    @NotNull(message = "Video ID is required")
    private Long videoId;

    @NotNull(message = "Customer email is required")
    private String customerEmail;

    @NotNull(message = "Payment intent ID is required")
    private String paymentIntentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Currency is required")
    private String currency = "usd";

    public VideoStripePaymentRequestDTO() {}

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
