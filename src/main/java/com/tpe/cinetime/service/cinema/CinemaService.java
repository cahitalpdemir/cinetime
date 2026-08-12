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
import com.tpe.cinetime.repository.cinema.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final HallRepository hallRepository;
    private final CinemaMapper cinemaMapper;

    @Transactional
    public ResponseMessage<CinemaResponseDTO> saveCinema(CinemaRequestDTO cinemaRequestDTO) {
        if (cinemaRepository.existsByPhone(cinemaRequestDTO.getPhone())) {
            throw new BadRequestException(ErrorMessages.CINEMA_PHONE_ALREADY_EXISTS);
        }

        Cinema cinema = cinemaMapper.mapCinemaRequestDTOToCinema(cinemaRequestDTO);
        Cinema savedCinema = cinemaRepository.save(cinema);

        return buildCinemaResponse(
                savedCinema,
                SuccessMessages.CINEMA_SAVED_SUCCESSFULLY,
                HttpStatus.CREATED
        );
    }

    @Transactional
    public ResponseMessage<CinemaResponseDTO> updateCinema(Long cinemaId, CinemaRequestDTO cinemaRequestDTO) {
        Cinema cinema = getCinemaEntityById(cinemaId);

        if (cinemaRepository.existsByPhoneAndIdNot(cinemaRequestDTO.getPhone(), cinemaId)) {
            throw new BadRequestException(ErrorMessages.CINEMA_PHONE_ALREADY_EXISTS);
        }

        cinema.setName(cinemaRequestDTO.getName());
        cinema.setCity(cinemaRequestDTO.getCity());
        cinema.setDistrict(cinemaRequestDTO.getDistrict());
        cinema.setAddress(cinemaRequestDTO.getAddress());
        cinema.setPhone(cinemaRequestDTO.getPhone());
        cinema.setLatitude(cinemaRequestDTO.getLatitude());
        cinema.setLongitude(cinemaRequestDTO.getLongitude());

        Cinema savedCinema = cinemaRepository.save(cinema);

        return buildCinemaResponse(
                savedCinema,
                SuccessMessages.CINEMA_UPDATED_SUCCESSFULLY,
                HttpStatus.OK
        );
    }

    @Transactional
    public ResponseMessage<CinemaResponseDTO> deleteCinema(Long cinemaId) {
        Cinema cinema = getCinemaEntityById(cinemaId);

        if (hallRepository.existsByCinema_Id(cinemaId)) {
            throw new BadRequestException(ErrorMessages.CINEMA_HAS_HALLS);
        }

        CinemaResponseDTO response = cinemaMapper.mapCinemaToResponseDTO(cinema);
        cinemaRepository.delete(cinema);

        return ResponseMessage.<CinemaResponseDTO>builder()
                .object(response)
                .message(SuccessMessages.CINEMA_DELETED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
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
        Cinema cinema = getCinemaEntityById(cinemaId);

        return buildCinemaResponse(
                cinema,
                SuccessMessages.CINEMA_FETCHED_SUCCESSFULLY,
                HttpStatus.OK
        );
    }

    public Cinema getCinemaEntityById(Long cinemaId) {
        return cinemaRepository.findById(cinemaId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.CINEMA_NOT_FOUND, cinemaId)));
    }

    private ResponseMessage<CinemaResponseDTO> buildCinemaResponse(
            Cinema cinema,
            String message,
            HttpStatus httpStatus
    ) {
        return ResponseMessage.<CinemaResponseDTO>builder()
                .object(cinemaMapper.mapCinemaToResponseDTO(cinema))
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}