package com.example.artgallery.service;

import com.example.artgallery.dto.OrderRequestDTO;
import com.example.artgallery.entity.Artwork;
import com.example.artgallery.entity.OrderRequest;
import com.example.artgallery.mapper.EntityMapperInt;
import com.example.artgallery.repository.ArtworkRepository;
import com.example.artgallery.repository.OrderRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderRequestService {

    private final OrderRequestRepository orderRequestRepository;
    private final ArtworkRepository artworkRepository;
    private final EntityMapperInt mapper;
    private final EmailService emailService;

    @Autowired
    public OrderRequestService(OrderRequestRepository orderRequestRepository,
                               ArtworkRepository artworkRepository,
                               EntityMapperInt mapper,
                               EmailService emailService) {
        this.orderRequestRepository = orderRequestRepository;
        this.artworkRepository = artworkRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }

    public List<OrderRequestDTO> getAll() {
        return orderRequestRepository.findAll().stream()
                .map(mapper::toOrderRequestDTO)
                .collect(Collectors.toList());
    }

    public Optional<OrderRequestDTO> getById(Long id) {
        return orderRequestRepository.findById(id)
                .map(mapper::toOrderRequestDTO);
    }

    public OrderRequest createOrder(OrderRequestDTO dto) {
        OrderRequest orderRequest = mapper.toOrderRequest(dto);
        List<Artwork> artworks = artworkRepository.findAllById(dto.getArtworkIds());
        orderRequest.setArtworks(artworks);
        orderRequest.setStatus("NEW");
        orderRequest.setTotalPrice(artworks.stream()
                .map(Artwork::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        orderRequest = orderRequestRepository.save(orderRequest);
        emailService.sendOrderNotification(orderRequest);
        return orderRequest;
    }

    public void delete(Long id) {
        orderRequestRepository.deleteById(id);
    }

    public OrderRequestDTO updateStatus(Long id, String status) {
        Optional<OrderRequest> orderOpt = orderRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found");
        }
        
        OrderRequest order = orderOpt.get();
        order.setStatus(status);
        order = orderRequestRepository.save(order);
        
        // Send email notification about status change
        emailService.sendOrderStatusUpdate(order);
        
        return mapper.toOrderRequestDTO(order);
    }
}
