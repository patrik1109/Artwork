package com.example.artgallery.service;

import com.example.artgallery.dto.PhotoPurchaseDTO;
import com.example.artgallery.dto.PurchaseRequestDTO;
import com.example.artgallery.entity.Photo;
import com.example.artgallery.entity.PhotoPurchase;
import com.example.artgallery.repository.PhotoPurchaseRepository;
import com.example.artgallery.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoPurchaseService {
    
    @Autowired
    private PhotoPurchaseRepository photoPurchaseRepository;
    
    @Autowired
    private PhotoRepository photoRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Create new purchase
    public PhotoPurchaseDTO createPurchase(PurchaseRequestDTO request) {
        // Find photo
        Optional<Photo> photoOpt = photoRepository.findById(request.getPhotoId());
        if (photoOpt.isEmpty()) {
            throw new RuntimeException("Photo not found");
        }
        
        Photo photo = photoOpt.get();
        
        // Check if there's already an active purchase
        Optional<PhotoPurchase> existingPurchase = photoPurchaseRepository.findActivePurchase(
            request.getCustomerEmail(), 
            request.getPhotoId(), 
            LocalDateTime.now()
        );
        
        if (existingPurchase.isPresent()) {
            throw new RuntimeException("You already have an active purchase for this photo");
        }
        
        // Create purchase
        PhotoPurchase purchase = new PhotoPurchase(
            photo, 
            request.getCustomerEmail(), 
            photo.getPrice(), 
            generateTransactionId()
        );
        
        // Save purchase
        purchase = photoPurchaseRepository.save(purchase);
        
        // Send confirmation email
        emailService.sendPurchaseConfirmation(request.getCustomerEmail(), photo.getTitle(), purchase.getTransactionId());
        
        return convertToDTO(purchase);
    }
    
    // Confirm payment
    public PhotoPurchaseDTO confirmPayment(String transactionId) {
        Optional<PhotoPurchase> purchaseOpt = photoPurchaseRepository.findByTransactionId(transactionId);
        if (purchaseOpt.isEmpty()) {
            throw new RuntimeException("Purchase not found");
        }
        
        PhotoPurchase purchase = purchaseOpt.get();
        purchase.setStatus(PhotoPurchase.PurchaseStatus.COMPLETED);
        purchase.setDownloadToken(generateDownloadToken());
        purchase.setTokenExpiry(LocalDateTime.now().plusDays(7)); // Token valid for 7 days
        
        purchase = photoPurchaseRepository.save(purchase);
        
        // Send purchase success email
        emailService.sendPurchaseSuccess(purchase.getCustomerEmail(), purchase.getPhoto().getTitle(), purchase.getTransactionId());
        
        return convertToDTO(purchase);
    }
    
    // Get download URL
    public String getDownloadUrl(String downloadToken) {
        Optional<PhotoPurchase> purchaseOpt = photoPurchaseRepository.findByDownloadToken(downloadToken);
        if (purchaseOpt.isEmpty()) {
            throw new RuntimeException("Invalid download token");
        }
        
        PhotoPurchase purchase = purchaseOpt.get();
        
        // Check token expiry
        if (purchase.getTokenExpiry().isBefore(LocalDateTime.now())) {
            purchase.setStatus(PhotoPurchase.PurchaseStatus.EXPIRED);
            photoPurchaseRepository.save(purchase);
            throw new RuntimeException("Download token has expired");
        }
        
        return purchase.getPhoto().getDownloadUrl();
    }
    
    // Get user purchases
    public List<PhotoPurchaseDTO> getUserPurchases(String customerEmail) {
        return photoPurchaseRepository.findByCustomerEmail(customerEmail)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Check if user can download photo
    public boolean canUserDownload(String customerEmail, Long photoId) {
        return photoPurchaseRepository.findActivePurchase(customerEmail, photoId, LocalDateTime.now()).isPresent();
    }
    
    // Clean up expired tokens
    public void cleanupExpiredTokens() {
        List<PhotoPurchase> expiredTokens = photoPurchaseRepository.findExpiredTokens(LocalDateTime.now());
        for (PhotoPurchase purchase : expiredTokens) {
            purchase.setStatus(PhotoPurchase.PurchaseStatus.EXPIRED);
        }
        photoPurchaseRepository.saveAll(expiredTokens);
    }
    
    // Get all purchases (for admin)
    public List<PhotoPurchaseDTO> getAllPurchases() {
        return photoPurchaseRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Confirm payment by ID (for admin)
    public PhotoPurchaseDTO confirmPayment(Long id) {
        Optional<PhotoPurchase> purchaseOpt = photoPurchaseRepository.findById(id);
        if (purchaseOpt.isEmpty()) {
            throw new RuntimeException("Purchase not found");
        }
        
        PhotoPurchase purchase = purchaseOpt.get();
        purchase.setStatus(PhotoPurchase.PurchaseStatus.COMPLETED);
        purchase.setDownloadToken(generateDownloadToken());
        purchase.setTokenExpiry(LocalDateTime.now().plusDays(7)); // Token valid for 7 days
        
        purchase = photoPurchaseRepository.save(purchase);
        
        // Send purchase success email
        emailService.sendPurchaseSuccess(purchase.getCustomerEmail(), purchase.getPhoto().getTitle(), purchase.getTransactionId());
        
        return convertToDTO(purchase);
    }
    
    // Helper methods
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateDownloadToken() {
        return "DL-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
    }
    
    private PhotoPurchaseDTO convertToDTO(PhotoPurchase purchase) {
        PhotoPurchaseDTO dto = new PhotoPurchaseDTO();
        dto.setId(purchase.getId());
        dto.setPhotoId(purchase.getPhoto().getId());
        dto.setPhotoTitle(purchase.getPhoto().getTitle());
        dto.setCustomerEmail(purchase.getCustomerEmail());
        dto.setAmountPaid(purchase.getAmountPaid());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setTransactionId(purchase.getTransactionId());
        dto.setDownloadToken(purchase.getDownloadToken());
        dto.setTokenExpiry(purchase.getTokenExpiry());
        dto.setStatus(purchase.getStatus());
        dto.setDownloadUrl(purchase.getPhoto().getDownloadUrl());
        dto.setIsExpired(dto.getIsExpired());
        dto.setCanDownload(dto.getCanDownload());
        
        return dto;
    }
} 