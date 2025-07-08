package com.example.artgallery.mapper;

import com.example.artgallery.dto.ArtworkDTO;
import com.example.artgallery.dto.OrderRequestDTO;
import com.example.artgallery.entity.Artwork;
import com.example.artgallery.entity.OrderRequest;
import com.example.artgallery.entity.Photo;
import com.example.artgallery.dto.PhotoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface EntityMapperInt {

    ArtworkDTO toArtworkDTO(Artwork artwork);

    Artwork toArtwork(ArtworkDTO dto);

    @Mapping(target = "artworkIds", expression = "java(orderRequest.getArtworks().stream().map(Artwork::getId).toList())")
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    OrderRequestDTO toOrderRequestDTO(OrderRequest orderRequest);

    @Mapping(target = "artworks", ignore = true) // ти встановиш вручну після мапінгу
    @Mapping(source = "totalPrice", target = "totalPrice")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    OrderRequest toOrderRequest(OrderRequestDTO dto);

    @Mapping(source = "price", target = "price")
    @Mapping(source = "downloadUrl", target = "downloadUrl")
    PhotoDTO toPhotoDTO(Photo photo);
    
    @Mapping(source = "price", target = "price")
    @Mapping(source = "downloadUrl", target = "downloadUrl")
    Photo toPhoto(PhotoDTO dto);
}


