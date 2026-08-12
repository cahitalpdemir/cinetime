package com.tpe.cinetime.service.cinema;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.Cinema;
import com.tpe.cinetime.entity.Hall;
import com.tpe.cinetime.entity.Seat;
import com.tpe.cinetime.enums.SeatType;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.request.cinema.HallRequestDTO;
import com.tpe.cinetime.payload.response.cinema.HallResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.cinema.HallRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import com.tpe.cinetime.repository.showtime.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final CinemaService cinemaService;
    private final ShowtimeRepository showtimeRepository;

    @Transactional
    public ResponseMessage<HallResponseDTO> saveHall(HallRequestDTO hallRequestDTO) {
        Cinema cinema = cinemaService.getCinemaEntityById(hallRequestDTO.getCinemaId());

        Hall hall = Hall.builder()
                .name(hallRequestDTO.getName())
                .hallType(hallRequestDTO.getHallType())
                .rows(hallRequestDTO.getRows())
                .seatsPerRow(hallRequestDTO.getSeatsPerRow())
                .cinema(cinema)
                .build();

        Hall savedHall = hallRepository.save(hall);

        int seatCount = generateSeats(savedHall);
        HallResponseDTO response = mapHallToResponse(savedHall, seatCount);

        return ResponseMessage.<HallResponseDTO>builder()
                .object(response)
                .message(SuccessMessages.HALL_SAVED_SUCCESSFULLY)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ResponseMessage<HallResponseDTO> updateHall(Long hallId, HallRequestDTO hallRequestDTO) {
        Hall hall = getHallEntityById(hallId);
        Cinema cinema = cinemaService.getCinemaEntityById(hallRequestDTO.getCinemaId());
        boolean seatLayoutChanged = isSeatLayoutChanged(hall, hallRequestDTO);

        if (seatLayoutChanged && showtimeRepository.existsByHallId(hallId)) {
            throw new BadRequestException(ErrorMessages.HALL_HAS_SHOWTIMES);
        }

        hall.setName(hallRequestDTO.getName());
        hall.setHallType(hallRequestDTO.getHallType());
        hall.setRows(hallRequestDTO.getRows());
        hall.setSeatsPerRow(hallRequestDTO.getSeatsPerRow());
        hall.setCinema(cinema);

        Hall savedHall = hallRepository.save(hall);
        int seatCount = savedHall.getRows() * savedHall.getSeatsPerRow();

        if (seatLayoutChanged) {
            seatRepository.deleteByHallId(savedHall.getId());
            seatCount = generateSeats(savedHall);
        }

        return ResponseMessage.<HallResponseDTO>builder()
                .object(mapHallToResponse(savedHall, seatCount))
                .message(SuccessMessages.HALL_UPDATED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<HallResponseDTO> deleteHall(Long hallId) {
        Hall hall = getHallEntityById(hallId);

        if (showtimeRepository.existsByHallId(hallId)) {
            throw new BadRequestException(ErrorMessages.HALL_HAS_SHOWTIMES);
        }

        HallResponseDTO response = mapHallToResponse(hall, hall.getRows() * hall.getSeatsPerRow());
        seatRepository.deleteByHallId(hallId);
        hallRepository.delete(hall);

        return ResponseMessage.<HallResponseDTO>builder()
                .object(response)
                .message(SuccessMessages.HALL_DELETED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional(readOnly = true)
    public ResponseMessage<List<HallResponseDTO>> getHallsByCinemaId(Long cinemaId) {
        cinemaService.getCinemaEntityById(cinemaId);

        List<HallResponseDTO> halls = hallRepository.findByCinema_IdOrderByNameAsc(cinemaId)
                .stream()
                .map(hall -> mapHallToResponse(hall, hall.getRows() * hall.getSeatsPerRow()))
                .collect(Collectors.toList());

        return ResponseMessage.<List<HallResponseDTO>>builder()
                .object(halls)
                .message(SuccessMessages.HALLS_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private Hall getHallEntityById(Long hallId) {
        return hallRepository.findById(hallId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.HALL_NOT_FOUND, hallId)));
    }

    private boolean isSeatLayoutChanged(Hall hall, HallRequestDTO hallRequestDTO) {
        return !Objects.equals(hall.getRows(), hallRequestDTO.getRows())
                || !Objects.equals(hall.getSeatsPerRow(), hallRequestDTO.getSeatsPerRow());
    }

    private HallResponseDTO mapHallToResponse(Hall hall, int seatCount) {
        return HallResponseDTO.builder()
                .id(hall.getId())
                .name(hall.getName())
                .hallType(hall.getHallType())
                .cinemaId(hall.getCinema().getId())
                .rows(hall.getRows())
                .seatsPerRow(hall.getSeatsPerRow())
                .capacity(hall.getRows() * hall.getSeatsPerRow())
                .createdSeatCount(seatCount)
                .build();
    }

    private int generateSeats(Hall hall) {
        List<Seat> seats = new ArrayList<>();
        int rows = hall.getRows();
        int seatsPerRow = hall.getSeatsPerRow();

        for (int row = 0; row < rows; row++) {
            String rowLetter = String.valueOf((char) ('A' + row));
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = Seat.builder()
                        .rowLetter(rowLetter)
                        .seatNumber(seatNum)
                        .seatType(SeatType.STANDARD)
                        .hall(hall)
                        .build();
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
        return seats.size();
    }
}