package com.example.artgallery.controller;

import com.example.artgallery.dto.AdminDashboardDTO;
import com.example.artgallery.dto.PhotoPurchaseDTO;
import com.example.artgallery.dto.OrderRequestDTO;
import com.example.artgallery.service.AdminService;
import com.example.artgallery.service.PhotoPurchaseService;
import com.example.artgallery.service.OrderRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "https://artwork-production-fec7.up.railway.app"})
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PhotoPurchaseService photoPurchaseService;

    @Autowired
    private OrderRequestService orderRequestService;

    // Dashboard статистика
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // Статистика продажів
    @GetMapping("/sales/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSalesStats(
            @RequestParam(required = false) String period) {
        return ResponseEntity.ok(adminService.getSalesStats(period));
    }

    // Всі покупки фотографій
    @GetMapping("/purchases")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PhotoPurchaseDTO>> getAllPurchases() {
        return ResponseEntity.ok(photoPurchaseService.getAllPurchases());
    }

    // Підтвердження платежу
    @PostMapping("/purchases/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhotoPurchaseDTO> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(photoPurchaseService.confirmPayment(id));
    }

    // Всі замовлення
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderRequestDTO>> getAllOrders() {
        return ResponseEntity.ok(orderRequestService.getAll());
    }

    // Зміна статусу замовлення
    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderRequestDTO> updateOrderStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        return ResponseEntity.ok(orderRequestService.updateStatus(id, status));
    }

    // Популярні фотографії
    @GetMapping("/photos/popular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPopularPhotos() {
        return ResponseEntity.ok(adminService.getPopularPhotos());
    }

    // Статистика по email
    @GetMapping("/analytics/email-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getEmailStats() {
        return ResponseEntity.ok(adminService.getEmailStats());
    }

    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().header("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\"").body("Logged out successfully");
    }
} 