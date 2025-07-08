package com.example.artgallery.repository;

import com.example.artgallery.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
}

