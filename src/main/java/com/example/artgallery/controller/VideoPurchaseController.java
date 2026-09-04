package com.example.artgallery.controller;

import com.example.artgallery.dto.VideoPurchaseDTO;
import com.example.artgallery.dto.VideoPurchaseRequestDTO;
import com.example.artgallery.entity.VideoPurchase;
import com.example.artgallery.repository.VideoPurchaseRepository;
import com.example.artgallery.service.VideoPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/video-purchases")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://artwork-production-fec7.up.railway.app",
    "https://mishvazovski.com",
    "https://www.mishvazovski.com"
}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class VideoPurchaseController {

    @Autowired
    private VideoPurchaseService videoPurchaseService;

    @Autowired
    private VideoPurchaseRepository videoPurchaseRepository;

    @PostMapping("/purchase")
    public ResponseEntity<VideoPurchaseDTO> createPurchase(@Valid @RequestBody VideoPurchaseRequestDTO request) {
        try {
            VideoPurchaseDTO purchase = videoPurchaseService.createPurchase(request);
            return ResponseEntity.ok(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<VideoPurchaseDTO> confirmPayment(@RequestParam String transactionId) {
        try {
            VideoPurchaseDTO purchase = videoPurchaseService.confirmPayment(transactionId);
            return ResponseEntity.ok(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<String> getDownloadUrl(@RequestParam String downloadToken) {
        try {
            String downloadUrl = videoPurchaseService.getDownloadUrl(downloadToken);
            return ResponseEntity.ok(downloadUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<VideoPurchaseDTO>> getUserPurchases(@PathVariable String email) {
        List<VideoPurchaseDTO> purchases = videoPurchaseService.getUserPurchases(email);
        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/can-download")
    public ResponseEntity<Boolean> canUserDownload(@RequestParam String email, @RequestParam Long videoId) {
        boolean canDownload = videoPurchaseService.canUserDownload(email, videoId);
        return ResponseEntity.ok(canDownload);
    }

    @PostMapping("/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredTokens() {
        videoPurchaseService.cleanupExpiredTokens();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestParam String downloadToken) {
        Optional<VideoPurchase> purchaseOpt = videoPurchaseRepository.findByDownloadToken(downloadToken);
        if (purchaseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VideoPurchase purchase = purchaseOpt.get();
        if (purchase.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        String downloadUrl = purchase.getVideo().getDownloadUrl();
        String fileName = Paths.get(downloadUrl).getFileName().toString();
        String resourcePath = "static" + downloadUrl;

        Resource resource;
        try {
            resource = new org.springframework.core.io.ClassPathResource(resourcePath);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = resolveContentType(fileName);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }

    private String resolveContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".mp4")) return "video/mp4";
        if (lower.endsWith(".webm")) return "video/webm";
        if (lower.endsWith(".mov")) return "video/quicktime";
        if (lower.endsWith(".avi")) return "video/x-msvideo";
        if (lower.endsWith(".mkv")) return "video/x-matroska";
        return "application/octet-stream";
    }
}
