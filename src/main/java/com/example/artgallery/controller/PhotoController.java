package com.example.artgallery.controller;

import com.example.artgallery.dto.PhotoDTO;
import com.example.artgallery.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @GetMapping
    public ResponseEntity<List<PhotoDTO>> getAll() {
        return ResponseEntity.ok(photoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDTO> getById(@PathVariable Long id) {
        PhotoDTO dto = photoService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PhotoDTO> create(@RequestBody PhotoDTO dto) {
        return ResponseEntity.ok(photoService.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 