package com.example.artgallery.controller;

import com.example.artgallery.dto.PhotoPurchaseDTO;
import com.example.artgallery.dto.PurchaseRequestDTO;
import com.example.artgallery.service.PhotoPurchaseService;
import com.example.artgallery.repository.PhotoPurchaseRepository;
import com.example.artgallery.entity.PhotoPurchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/photo-purchases")
@CrossOrigin(origins = {"http://localhost:3000", "https://artwork-production-fec7.up.railway.app"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PhotoPurchaseController {
    
    @Autowired
    private PhotoPurchaseService photoPurchaseService;
    
    @Autowired
    private PhotoPurchaseRepository photoPurchaseRepository;
    
    // Створити нову покупку
    @PostMapping("/purchase")
    public ResponseEntity<PhotoPurchaseDTO> createPurchase(@Valid @RequestBody PurchaseRequestDTO request) {
        try {
            PhotoPurchaseDTO purchase = photoPurchaseService.createPurchase(request);
            return ResponseEntity.ok(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Підтвердити оплату
    @PostMapping("/confirm-payment")
    public ResponseEntity<PhotoPurchaseDTO> confirmPayment(@RequestParam String transactionId) {
        try {
            PhotoPurchaseDTO purchase = photoPurchaseService.confirmPayment(transactionId);
            return ResponseEntity.ok(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Отримати посилання для скачування
    @GetMapping("/download")
    public ResponseEntity<String> getDownloadUrl(@RequestParam String downloadToken) {
        try {
            String downloadUrl = photoPurchaseService.getDownloadUrl(downloadToken);
            return ResponseEntity.ok(downloadUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Отримати покупки користувача
    @GetMapping("/user/{email}")
    public ResponseEntity<List<PhotoPurchaseDTO>> getUserPurchases(@PathVariable String email) {
        List<PhotoPurchaseDTO> purchases = photoPurchaseService.getUserPurchases(email);
        return ResponseEntity.ok(purchases);
    }
    
    // Перевірити, чи може користувач скачати фото
    @GetMapping("/can-download")
    public ResponseEntity<Boolean> canUserDownload(@RequestParam String email, @RequestParam Long photoId) {
        boolean canDownload = photoPurchaseService.canUserDownload(email, photoId);
        return ResponseEntity.ok(canDownload);
    }
    
    // Очистити застарілі токени (для адміністратора)
    @PostMapping("/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredTokens() {
        photoPurchaseService.cleanupExpiredTokens();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestParam String downloadToken) {
        Optional<PhotoPurchase> purchaseOpt = photoPurchaseRepository.findByDownloadToken(downloadToken);
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PhotoPurchase purchase = purchaseOpt.get();
        if (purchase.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }
        
        String downloadUrl = purchase.getPhoto().getDownloadUrl();
        String fileName = Paths.get(downloadUrl).getFileName().toString();
        
        // Використовуємо classpath ресурси замість хардкодованого шляху
        String resourcePath = "static" + downloadUrl; // downloadUrl починається з /images/photos/
        
        Resource resource;
        try {
            resource = new org.springframework.core.io.ClassPathResource(resourcePath);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        String contentType = "image/jpeg"; // припускаємо що всі файли jpeg
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }
} 