package com.tpe.cinetime.service.cinema;

import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.Cinema;
import com.tpe.cinetime.entity.Hall;
import com.tpe.cinetime.entity.Seat;
import com.tpe.cinetime.enums.SeatType;
import com.tpe.cinetime.payload.request.cinema.HallRequestDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.cinema.HallRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final CinemaService cinemaService;

    @Transactional
    public ResponseMessage<Void> saveHall(HallRequestDTO hallRequestDTO) {
        Cinema cinema = cinemaService.getCinemaEntityById(hallRequestDTO.getCinemaId());

        Hall hall = Hall.builder()
                .name(hallRequestDTO.getName())
                .hallType(hallRequestDTO.getHallType())
                .rows(hallRequestDTO.getRows())
                .seatsPerRow(hallRequestDTO.getSeatsPerRow())
                .cinema(cinema)
                .build();

        Hall savedHall = hallRepository.save(hall);

        // T11: rows x seatsPerRow formülüyle Seat kayıtlarını otomatik üret
        generateSeats(savedHall);

        return ResponseMessage.<Void>builder()
                .message(SuccessMessages.HALL_SAVED_SUCCESSFULLY)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    private void generateSeats(Hall hall) {
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
    }
}
