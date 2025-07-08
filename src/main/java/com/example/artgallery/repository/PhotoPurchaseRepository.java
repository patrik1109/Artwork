package com.example.artgallery.repository;

import com.example.artgallery.entity.PhotoPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoPurchaseRepository extends JpaRepository<PhotoPurchase, Long> {
    
    // Знайти покупки за email користувача
    List<PhotoPurchase> findByCustomerEmail(String customerEmail);
    
    // Знайти покупки за фото
    List<PhotoPurchase> findByPhotoId(Long photoId);
    
    // Знайти активну покупку за email та photoId
    @Query("SELECT pp FROM PhotoPurchase pp WHERE pp.customerEmail = :email AND pp.photo.id = :photoId AND pp.status = 'COMPLETED' AND pp.tokenExpiry > :now")
    Optional<PhotoPurchase> findActivePurchase(@Param("email") String email, @Param("photoId") Long photoId, @Param("now") LocalDateTime now);
    
    // Знайти покупку за токен
    Optional<PhotoPurchase> findByDownloadToken(String downloadToken);
    
    // Знайти покупку за transaction ID
    Optional<PhotoPurchase> findByTransactionId(String transactionId);
    
    // Знайти застарілі токени
    @Query("SELECT pp FROM PhotoPurchase pp WHERE pp.tokenExpiry < :now AND pp.status = 'COMPLETED'")
    List<PhotoPurchase> findExpiredTokens(@Param("now") LocalDateTime now);
    
    // Знайти покупки за статус
    List<PhotoPurchase> findByStatus(PhotoPurchase.PurchaseStatus status);
} 