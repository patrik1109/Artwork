package com.example.artgallery.repository;

import com.example.artgallery.entity.VideoPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPurchaseRepository extends JpaRepository<VideoPurchase, Long> {

    List<VideoPurchase> findByCustomerEmail(String customerEmail);

    List<VideoPurchase> findByVideoId(Long videoId);

    @Query("SELECT vp FROM VideoPurchase vp WHERE vp.customerEmail = :email AND vp.video.id = :videoId AND vp.status = 'COMPLETED' AND vp.tokenExpiry > :now")
    Optional<VideoPurchase> findActivePurchase(@Param("email") String email, @Param("videoId") Long videoId, @Param("now") LocalDateTime now);

    Optional<VideoPurchase> findByDownloadToken(String downloadToken);

    Optional<VideoPurchase> findByTransactionId(String transactionId);

    @Query("SELECT vp FROM VideoPurchase vp WHERE vp.tokenExpiry < :now AND vp.status = 'COMPLETED'")
    List<VideoPurchase> findExpiredTokens(@Param("now") LocalDateTime now);

    List<VideoPurchase> findByStatus(VideoPurchase.PurchaseStatus status);
}
