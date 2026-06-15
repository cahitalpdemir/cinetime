package com.tpe.cinetime.service.cinema;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.Cinema;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.mapper.CinemaMapper;
import com.tpe.cinetime.payload.request.cinema.CinemaRequestDTO;
import com.tpe.cinetime.payload.response.cinema.CinemaResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.cinema.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    public ResponseMessage<CinemaResponseDTO> saveCinema(CinemaRequestDTO cinemaRequestDTO) {
        if (cinemaRepository.existsByPhone(cinemaRequestDTO.getPhone())) {
            throw new BadRequestException(ErrorMessages.CINEMA_PHONE_ALREADY_EXISTS);
        }

        Cinema cinema = cinemaMapper.mapCinemaRequestDTOToCinema(cinemaRequestDTO);
        Cinema savedCinema = cinemaRepository.save(cinema);

        return ResponseMessage.<CinemaResponseDTO>builder()
                .object(cinemaMapper.mapCinemaToResponseDTO(savedCinema))
                .message(SuccessMessages.CINEMA_SAVED_SUCCESSFULLY)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    public ResponseMessage<List<CinemaResponseDTO>> getAllCinemas() {
        List<CinemaResponseDTO> cinemas = cinemaRepository.findAll()
                .stream()
                .map(cinemaMapper::mapCinemaToResponseDTO)
                .collect(Collectors.toList());

        return ResponseMessage.<List<CinemaResponseDTO>>builder()
                .object(cinemas)
                .message(SuccessMessages.CINEMAS_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<CinemaResponseDTO> getCinemaById(Long cinemaId) {
        Cinema cinema = cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.CINEMA_NOT_FOUND, cinemaId)));

        return ResponseMessage.<CinemaResponseDTO>builder()
                .object(cinemaMapper.mapCinemaToResponseDTO(cinema))
                .message(SuccessMessages.CINEMA_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public Cinema getCinemaEntityById(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.CINEMA_NOT_FOUND, cinemaId)));
    }
}
