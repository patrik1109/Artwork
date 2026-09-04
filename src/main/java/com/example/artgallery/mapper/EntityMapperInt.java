package com.example.artgallery.mapper;

import com.example.artgallery.dto.ArtworkDTO;
import com.example.artgallery.dto.OrderRequestDTO;
import com.example.artgallery.entity.Artwork;
import com.example.artgallery.entity.OrderRequest;
import com.example.artgallery.entity.Photo;
import com.example.artgallery.dto.PhotoDTO;
import com.example.artgallery.entity.Video;
import com.example.artgallery.dto.VideoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface EntityMapperInt {

	ArtworkDTO toArtworkDTO(Artwork artwork);

	Artwork toArtwork(ArtworkDTO dto);

	@Mapping(target = "artworkIds", expression = "java(orderRequest.getArtworks().stream().map(Artwork::getId).toList())")
	@Mapping(source = "totalPrice", target = "totalPrice")
	@Mapping(source = "phoneNumber", target = "phoneNumber")
	@Mapping(source = "shippingAddress", target = "shippingAddress")
	@Mapping(source = "country", target = "country")
	@Mapping(source = "city", target = "city")
	@Mapping(source = "postalCode", target = "postalCode")
	OrderRequestDTO toOrderRequestDTO(OrderRequest orderRequest);

	@Mapping(target = "artworks", ignore = true)
	@Mapping(source = "totalPrice", target = "totalPrice")
	@Mapping(source = "phoneNumber", target = "phoneNumber")
	@Mapping(source = "shippingAddress", target = "shippingAddress")
	@Mapping(source = "country", target = "country")
	@Mapping(source = "city", target = "city")
	@Mapping(source = "postalCode", target = "postalCode")
	OrderRequest toOrderRequest(OrderRequestDTO dto);

	@Mapping(source = "price", target = "price")
	@Mapping(source = "downloadUrl", target = "downloadUrl")
	PhotoDTO toPhotoDTO(Photo photo);
	
	@Mapping(source = "price", target = "price")
	@Mapping(source = "downloadUrl", target = "downloadUrl")
	Photo toPhoto(PhotoDTO dto);

	@Mapping(source = "price", target = "price")
	@Mapping(source = "downloadUrl", target = "downloadUrl")
	VideoDTO toVideoDTO(Video video);

	@Mapping(source = "price", target = "price")
	@Mapping(source = "downloadUrl", target = "downloadUrl")
	Video toVideo(VideoDTO dto);
}


