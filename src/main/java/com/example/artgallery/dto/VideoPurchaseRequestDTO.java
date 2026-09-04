package com.example.artgallery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class VideoPurchaseRequestDTO {
    @NotNull(message = "Video ID is required")
    private Long videoId;

    @NotNull(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    private String paymentMethod;
    private String currency = "USD";

    public VideoPurchaseRequestDTO() {}

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
