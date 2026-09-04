package com.example.artgallery.service;

import com.example.artgallery.dto.VideoPurchaseDTO;
import com.example.artgallery.dto.VideoPurchaseRequestDTO;
import com.example.artgallery.entity.Video;
import com.example.artgallery.entity.VideoPurchase;
import com.example.artgallery.repository.VideoPurchaseRepository;
import com.example.artgallery.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoPurchaseService {

    @Autowired
    private VideoPurchaseRepository videoPurchaseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private EmailService emailService;

    public VideoPurchaseDTO createPurchase(VideoPurchaseRequestDTO request) {
        Optional<Video> videoOpt = videoRepository.findById(request.getVideoId());
        if (videoOpt.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        Video video = videoOpt.get();

        VideoPurchase purchase = new VideoPurchase(
            video,
            request.getCustomerEmail(),
            video.getPrice(),
            generateTransactionId()
        );

        purchase = videoPurchaseRepository.save(purchase);

        emailService.sendPurchaseConfirmation(request.getCustomerEmail(), video.getTitle(), purchase.getTransactionId());

        return convertToDTO(purchase);
    }

    public VideoPurchaseDTO confirmPayment(String transactionId) {
        Optional<VideoPurchase> purchaseOpt = videoPurchaseRepository.findByTransactionId(transactionId);
        if (purchaseOpt.isEmpty()) {
            throw new RuntimeException("Purchase not found");
        }

        VideoPurchase purchase = purchaseOpt.get();
        purchase.setStatus(VideoPurchase.PurchaseStatus.COMPLETED);
        purchase.setDownloadToken(generateDownloadToken());
        purchase.setTokenExpiry(LocalDateTime.now().plusDays(7));

        purchase = videoPurchaseRepository.save(purchase);

        emailService.sendPurchaseSuccess(purchase.getCustomerEmail(), purchase.getVideo().getTitle(), purchase.getTransactionId());

        return convertToDTO(purchase);
    }

    public String getDownloadUrl(String downloadToken) {
        Optional<VideoPurchase> purchaseOpt = videoPurchaseRepository.findByDownloadToken(downloadToken);
        if (purchaseOpt.isEmpty()) {
            throw new RuntimeException("Invalid download token");
        }

        VideoPurchase purchase = purchaseOpt.get();

        if (purchase.getTokenExpiry().isBefore(LocalDateTime.now())) {
            purchase.setStatus(VideoPurchase.PurchaseStatus.EXPIRED);
            videoPurchaseRepository.save(purchase);
            throw new RuntimeException("Download token has expired");
        }

        return purchase.getVideo().getDownloadUrl();
    }

    public List<VideoPurchaseDTO> getUserPurchases(String customerEmail) {
        return videoPurchaseRepository.findByCustomerEmail(customerEmail)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public boolean canUserDownload(String customerEmail, Long videoId) {
        return videoPurchaseRepository.findActivePurchase(customerEmail, videoId, LocalDateTime.now()).isPresent();
    }

    public void cleanupExpiredTokens() {
        List<VideoPurchase> expiredTokens = videoPurchaseRepository.findExpiredTokens(LocalDateTime.now());
        for (VideoPurchase purchase : expiredTokens) {
            purchase.setStatus(VideoPurchase.PurchaseStatus.EXPIRED);
        }
        videoPurchaseRepository.saveAll(expiredTokens);
    }

    public List<VideoPurchaseDTO> getAllPurchases() {
        return videoPurchaseRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public VideoPurchaseDTO confirmPayment(Long id) {
        Optional<VideoPurchase> purchaseOpt = videoPurchaseRepository.findById(id);
        if (purchaseOpt.isEmpty()) {
            throw new RuntimeException("Purchase not found");
        }

        VideoPurchase purchase = purchaseOpt.get();
        purchase.setStatus(VideoPurchase.PurchaseStatus.COMPLETED);
        purchase.setDownloadToken(generateDownloadToken());
        purchase.setTokenExpiry(LocalDateTime.now().plusDays(7));

        purchase = videoPurchaseRepository.save(purchase);

        emailService.sendPurchaseSuccess(purchase.getCustomerEmail(), purchase.getVideo().getTitle(), purchase.getTransactionId());

        return convertToDTO(purchase);
    }

    private String generateTransactionId() {
        return "VTXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateDownloadToken() {
        return "VDL-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
    }

    private VideoPurchaseDTO convertToDTO(VideoPurchase purchase) {
        VideoPurchaseDTO dto = new VideoPurchaseDTO();
        dto.setId(purchase.getId());
        dto.setVideoId(purchase.getVideo().getId());
        dto.setVideoTitle(purchase.getVideo().getTitle());
        dto.setCustomerEmail(purchase.getCustomerEmail());
        dto.setAmountPaid(purchase.getAmountPaid());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setTransactionId(purchase.getTransactionId());
        dto.setDownloadToken(purchase.getDownloadToken());
        dto.setTokenExpiry(purchase.getTokenExpiry());
        dto.setStatus(purchase.getStatus());
        dto.setDownloadUrl(purchase.getVideo().getDownloadUrl());
        dto.setIsExpired(dto.getIsExpired());
        dto.setCanDownload(dto.getCanDownload());
        return dto;
    }
}
