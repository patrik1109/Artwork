package com.example.artgallery.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AdminDashboardDTO {
    private Long totalPhotos;
    private Long totalPurchases;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private List<Map<String, Object>> recentPurchases;
    private List<Map<String, Object>> popularPhotos;
    private Map<String, Long> salesByStatus;
    private Map<String, BigDecimal> revenueByMonth;

    public Long getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(Long totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public Long getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(Long totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<Map<String, Object>> getRecentPurchases() {
        return recentPurchases;
    }

    public void setRecentPurchases(List<Map<String, Object>> recentPurchases) {
        this.recentPurchases = recentPurchases;
    }

    public List<Map<String, Object>> getPopularPhotos() {
        return popularPhotos;
    }

    public void setPopularPhotos(List<Map<String, Object>> popularPhotos) {
        this.popularPhotos = popularPhotos;
    }

    public Map<String, Long> getSalesByStatus() {
        return salesByStatus;
    }

    public void setSalesByStatus(Map<String, Long> salesByStatus) {
        this.salesByStatus = salesByStatus;
    }

    public Map<String, BigDecimal> getRevenueByMonth() {
        return revenueByMonth;
    }

    public void setRevenueByMonth(Map<String, BigDecimal> revenueByMonth) {
        this.revenueByMonth = revenueByMonth;
    }
} 