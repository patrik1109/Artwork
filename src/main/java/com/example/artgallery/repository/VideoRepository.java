package com.example.artgallery.repository;

import com.example.artgallery.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
