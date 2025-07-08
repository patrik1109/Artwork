package com.example.artgallery.dto;

import java.math.BigDecimal;

public class PhotoDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String photographer;
    private BigDecimal price;
    private String downloadUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPhotographer() { return photographer; }
    public void setPhotographer(String photographer) { this.photographer = photographer; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public PhotoDTO() {}
    public PhotoDTO(Long id, String title, String description, String imageUrl, String photographer, BigDecimal price, String downloadUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.photographer = photographer;
        this.price = price;
        this.downloadUrl = downloadUrl;
    }
} 