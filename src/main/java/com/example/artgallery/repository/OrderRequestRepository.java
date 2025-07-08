package com.example.artgallery.repository;

import com.example.artgallery.entity.OrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRequestRepository extends JpaRepository<OrderRequest, Long> {
}
