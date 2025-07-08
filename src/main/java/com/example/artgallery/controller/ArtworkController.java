package com.example.artgallery.controller;

import com.example.artgallery.dto.ArtworkDTO;
import com.example.artgallery.entity.Artwork;
import com.example.artgallery.mapper.EntityMapperInt;
import com.example.artgallery.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    @Autowired
    private ArtworkRepository artworkRepository;
    @Autowired
    private EntityMapperInt mapper;


    @PostMapping
    public ResponseEntity<ArtworkDTO> createArtwork(@RequestBody ArtworkDTO dto) {
        Artwork artwork = mapper.toArtwork(dto);
        Artwork saved = artworkRepository.save(artwork);
        return ResponseEntity.ok(mapper.toArtworkDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtworkDTO> getArtwork(@PathVariable Long id) {
        return artworkRepository.findById(id)
                .map(mapper::toArtworkDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<ArtworkDTO>> getAllArtworks() {
        List<Artwork> artworks = artworkRepository.findAll();
        List<ArtworkDTO> dtos = artworks.stream()
                .map(mapper::toArtworkDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}
