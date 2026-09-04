package com.example.artgallery.controller;

import com.example.artgallery.dto.VideoDTO;
import com.example.artgallery.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @GetMapping
    public ResponseEntity<List<VideoDTO>> getAll() {
        return ResponseEntity.ok(videoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getById(@PathVariable Long id) {
        VideoDTO dto = videoService.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<VideoDTO> create(@RequestBody VideoDTO dto) {
        return ResponseEntity.ok(videoService.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        videoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
