package com.example.artgallery.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String imageUrl; // thumbnail preview
    private String creator;
    private BigDecimal price;
    private String downloadUrl; // full video file path

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public Video() {}
    public Video(Long id, String title, String description, String imageUrl, String creator, BigDecimal price, String downloadUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.creator = creator;
        this.price = price;
        this.downloadUrl = downloadUrl;
    }
}
