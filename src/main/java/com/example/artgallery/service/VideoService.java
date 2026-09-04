package com.example.artgallery.service;

import com.example.artgallery.dto.VideoDTO;
import com.example.artgallery.entity.Video;
import com.example.artgallery.mapper.EntityMapperInt;
import com.example.artgallery.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private EntityMapperInt mapper;

    public List<VideoDTO> getAll() {
        return videoRepository.findAll().stream().map(mapper::toVideoDTO).collect(Collectors.toList());
    }

    public VideoDTO getById(Long id) {
        return videoRepository.findById(id).map(mapper::toVideoDTO).orElse(null);
    }

    public VideoDTO create(VideoDTO dto) {
        Video video = mapper.toVideo(dto);
        return mapper.toVideoDTO(videoRepository.save(video));
    }

    public void delete(Long id) {
        videoRepository.deleteById(id);
    }
}
