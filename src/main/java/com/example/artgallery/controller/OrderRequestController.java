package com.example.artgallery.controller;

import com.example.artgallery.dto.OrderRequestDTO;
import com.example.artgallery.entity.OrderRequest;
import com.example.artgallery.mapper.EntityMapperInt;
import com.example.artgallery.repository.OrderRequestRepository;
import com.example.artgallery.service.OrderRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRequestController {

    private final OrderRequestService service;

    public OrderRequestController(OrderRequestService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderRequest> create(@RequestBody @Valid OrderRequestDTO dto) {
        OrderRequest created = service.createOrder(dto);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
