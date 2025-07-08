package com.example.artgallery.service;

import com.example.artgallery.dto.PhotoDTO;
import com.example.artgallery.entity.Photo;
import com.example.artgallery.mapper.EntityMapperInt;
import com.example.artgallery.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {
    @Autowired
    private PhotoRepository photoRepository;
    @Autowired
    private EntityMapperInt mapper;

    public List<PhotoDTO> getAll() {
        return photoRepository.findAll().stream().map(mapper::toPhotoDTO).collect(Collectors.toList());
    }
    public PhotoDTO getById(Long id) {
        return photoRepository.findById(id).map(mapper::toPhotoDTO).orElse(null);
    }
    public PhotoDTO create(PhotoDTO dto) {
        Photo photo = mapper.toPhoto(dto);
        return mapper.toPhotoDTO(photoRepository.save(photo));
    }
    public void delete(Long id) {
        photoRepository.deleteById(id);
    }
} 