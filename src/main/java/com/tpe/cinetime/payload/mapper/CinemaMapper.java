package com.tpe.cinetime.payload.mapper;

import org.springframework.stereotype.Component;

import com.tpe.cinetime.entity.Cinema;
import com.tpe.cinetime.payload.request.cinema.CinemaRequestDTO;
import com.tpe.cinetime.payload.response.cinema.CinemaResponseDTO;

@Component

public class CinemaMapper {
    
    public Cinema mapCinemaRequestDTOToCinema(CinemaRequestDTO cinemaRequestDTO){
        return Cinema.builder()
                .name(cinemaRequestDTO.getName())
                .city(cinemaRequestDTO.getCity())
                .district(cinemaRequestDTO.getDistrict())
                .address(cinemaRequestDTO.getAddress())
                .phone(cinemaRequestDTO.getPhone())
                .latitude(cinemaRequestDTO.getLatitude())
                .longitude(cinemaRequestDTO.getLongitude())
                .build();

    }
    public CinemaResponseDTO mapCinemaToResponseDTO(Cinema cinema) {
        return CinemaResponseDTO.builder()
            .id(cinema.getId())
            .name(cinema.getName())
            .city(cinema.getCity())
            .district(cinema.getDistrict())
            .address(cinema.getAddress())
            .phone(cinema.getPhone())
            .latitude(cinema.getLatitude())
            .longitude(cinema.getLongitude())
            .build();
}



}
