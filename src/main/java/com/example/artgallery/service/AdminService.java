package com.example.artgallery.service;

import com.example.artgallery.dto.AdminDashboardDTO;
import com.example.artgallery.repository.PhotoRepository;
import com.example.artgallery.repository.PhotoPurchaseRepository;
import com.example.artgallery.repository.OrderRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoPurchaseRepository photoPurchaseRepository;

    @Autowired
    private OrderRequestRepository orderRequestRepository;

    public AdminDashboardDTO getDashboardStats() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();
        
        // Базова статистика
        dashboard.setTotalPhotos(photoRepository.count());
        dashboard.setTotalPurchases(photoPurchaseRepository.count());
        dashboard.setTotalOrders(orderRequestRepository.count());
        
        // Загальний дохід
        BigDecimal totalRevenue = photoPurchaseRepository.findAll().stream()
                .filter(purchase -> "COMPLETED".equals(purchase.getStatus().name()))
                .map(purchase -> purchase.getAmountPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTotalRevenue(totalRevenue);
        
        // Місячний дохід
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        BigDecimal monthlyRevenue = photoPurchaseRepository.findAll().stream()
                .filter(purchase -> "COMPLETED".equals(purchase.getStatus().name()))
                .filter(purchase -> purchase.getPurchaseDate().isAfter(monthStart))
                .map(purchase -> purchase.getAmountPaid())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setMonthlyRevenue(monthlyRevenue);
        
        // Останні покупки
        List<Map<String, Object>> recentPurchases = photoPurchaseRepository.findAll().stream()
                .sorted((p1, p2) -> p2.getPurchaseDate().compareTo(p1.getPurchaseDate()))
                .limit(5)
                .map(purchase -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", purchase.getId());
                    map.put("photoTitle", purchase.getPhoto().getTitle());
                    map.put("customerEmail", purchase.getCustomerEmail());
                    map.put("amount", purchase.getAmountPaid());
                    map.put("date", purchase.getPurchaseDate());
                    map.put("status", purchase.getStatus());
                    return map;
                })
                .collect(Collectors.toList());
        dashboard.setRecentPurchases(recentPurchases);
        
        // Популярні фотографії
        List<Map<String, Object>> popularPhotos = photoRepository.findAll().stream()
                .map(photo -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", photo.getId());
                    map.put("title", photo.getTitle());
                    map.put("photographer", photo.getPhotographer());
                    map.put("price", photo.getPrice());
                    Long photoId = photo.getId();
                    long purchaseCount = photoPurchaseRepository.findAll().stream()
                            .filter(purchase -> purchase.getPhoto().getId().equals(photoId))
                            .count();
                    map.put("purchaseCount", purchaseCount);
                    return map;
                })
                .sorted((p1, p2) -> Long.compare((Long) p2.get("purchaseCount"), (Long) p1.get("purchaseCount")))
                .limit(5)
                .collect(Collectors.toList());
        dashboard.setPopularPhotos(popularPhotos);
        
        // Статистика по статусах
        Map<String, Long> salesByStatus = photoPurchaseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    purchase -> purchase.getStatus().name(),
                    Collectors.counting()
                ));
        dashboard.setSalesByStatus(salesByStatus);
        
        // Дохід по місяцях
        Map<String, BigDecimal> revenueByMonth = photoPurchaseRepository.findAll().stream()
                .filter(purchase -> "COMPLETED".equals(purchase.getStatus().name()))
                .collect(Collectors.groupingBy(
                    purchase -> purchase.getPurchaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    Collectors.reducing(BigDecimal.ZERO, purchase -> purchase.getAmountPaid(), BigDecimal::add)
                ));
        dashboard.setRevenueByMonth(revenueByMonth);
        
        return dashboard;
    }

    public Map<String, Object> getSalesStats(String period) {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime tempStartDate = LocalDateTime.now();
        if ("week".equals(period)) {
            tempStartDate = tempStartDate.minusWeeks(1);
        } else if ("month".equals(period)) {
            tempStartDate = tempStartDate.minusMonths(1);
        } else if ("year".equals(period)) {
            tempStartDate = tempStartDate.minusYears(1);
        } else {
            tempStartDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        }
        final LocalDateTime startDate = tempStartDate;
        
        List<Object> sales = photoPurchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getPurchaseDate().isAfter(startDate))
                .map(purchase -> {
                    Map<String, Object> sale = new HashMap<>();
                    sale.put("date", purchase.getPurchaseDate());
                    sale.put("amount", purchase.getAmountPaid());
                    sale.put("status", purchase.getStatus());
                    sale.put("photoTitle", purchase.getPhoto().getTitle());
                    return sale;
                })
                .collect(Collectors.toList());
        
        stats.put("sales", sales);
        stats.put("period", period);
        stats.put("totalSales", sales.size());
        
        return stats;
    }

    public Map<String, Object> getPopularPhotos() {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> photos = photoRepository.findAll().stream()
                .map(photo -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", photo.getId());
                    map.put("title", photo.getTitle());
                    map.put("photographer", photo.getPhotographer());
                    map.put("price", photo.getPrice());
                    Long photoId = photo.getId();
                    long purchaseCount = photoPurchaseRepository.findAll().stream()
                            .filter(purchase -> purchase.getPhoto().getId().equals(photoId))
                            .count();
                    map.put("purchaseCount", purchaseCount);
                    BigDecimal totalRevenue = photoPurchaseRepository.findAll().stream()
                            .filter(purchase -> purchase.getPhoto().getId().equals(photoId))
                            .filter(purchase -> "COMPLETED".equals(purchase.getStatus().name()))
                            .map(purchase -> purchase.getAmountPaid())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    map.put("totalRevenue", totalRevenue);
                    
                    return map;
                })
                .sorted((p1, p2) -> Long.compare((Long) p2.get("purchaseCount"), (Long) p1.get("purchaseCount")))
                .collect(Collectors.toList());
        
        result.put("photos", photos);
        return result;
    }

    public Map<String, Object> getEmailStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Статистика по email доменах
        Map<String, Long> domainStats = photoPurchaseRepository.findAll().stream()
                .map(purchase -> {
                    String email = purchase.getCustomerEmail();
                    return email != null && email.contains("@") ? 
                           email.substring(email.indexOf("@") + 1) : "unknown";
                })
                .collect(Collectors.groupingBy(domain -> domain, Collectors.counting()));
        
        stats.put("domainStats", domainStats);
        
        // Топ email адрес
        List<Map<String, Object>> topEmails = photoPurchaseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    purchase -> purchase.getCustomerEmail(),
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("email", entry.getKey());
                    map.put("purchaseCount", entry.getValue());
                    return map;
                })
                .sorted((e1, e2) -> Long.compare((Long) e2.get("purchaseCount"), (Long) e1.get("purchaseCount")))
                .limit(10)
                .collect(Collectors.toList());
        
        stats.put("topEmails", topEmails);
        
        return stats;
    }
} 