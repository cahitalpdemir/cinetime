package com.tpe.cinetime.service.cinema;

import com.tpe.cinetime.entity.Cinema;
import com.tpe.cinetime.entity.Hall;
import com.tpe.cinetime.enums.HallType;
import com.tpe.cinetime.payload.request.cinema.HallRequestDTO;
import com.tpe.cinetime.payload.response.cinema.HallResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.cinema.HallRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import com.tpe.cinetime.repository.showtime.ShowtimeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HallServiceTest {

    private final HallRepository hallRepository = mock(HallRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final CinemaService cinemaService = mock(CinemaService.class);
    private final ShowtimeRepository showtimeRepository = mock(ShowtimeRepository.class);
    private final HallService hallService = new HallService(
            hallRepository,
            seatRepository,
            cinemaService,
            showtimeRepository
    );

    @Test
    void saveHallReturnsGeneratedIdAndSeatCount() {
        Cinema cinema = Cinema.builder().id(4L).name("CineTime Kadikoy").build();
        HallRequestDTO request = HallRequestDTO.builder()
                .name("Hall 1")
                .hallType(HallType.IMAX)
                .rows(2)
                .seatsPerRow(3)
                .cinemaId(4L)
                .build();

        when(cinemaService.getCinemaEntityById(4L)).thenReturn(cinema);
        when(hallRepository.save(any(Hall.class))).thenAnswer(invocation -> {
            Hall hall = invocation.getArgument(0);
            hall.setId(9L);
            return hall;
        });

        ResponseMessage<HallResponseDTO> result = hallService.saveHall(request);

        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getObject().getId()).isEqualTo(9L);
        assertThat(result.getObject().getHallType()).isEqualTo(HallType.IMAX);
        assertThat(result.getObject().getCinemaId()).isEqualTo(4L);
        assertThat(result.getObject().getRows()).isEqualTo(2);
        assertThat(result.getObject().getSeatsPerRow()).isEqualTo(3);
        assertThat(result.getObject().getCapacity()).isEqualTo(6);
        assertThat(result.getObject().getCreatedSeatCount()).isEqualTo(6);
    }

    @Test
    void updateHallRegeneratesSeatsWhenLayoutChanges() {
        Cinema cinema = Cinema.builder().id(4L).name("CineTime Kadikoy").build();
        Hall hall = Hall.builder()
                .id(9L)
                .name("Salon 1")
                .hallType(HallType.STANDARD)
                .rows(2)
                .seatsPerRow(3)
                .cinema(cinema)
                .build();
        HallRequestDTO request = HallRequestDTO.builder()
                .name("Salon 1 Updated")
                .hallType(HallType.IMAX)
                .rows(3)
                .seatsPerRow(4)
                .cinemaId(4L)
                .build();

        when(hallRepository.findById(9L)).thenReturn(Optional.of(hall));
        when(cinemaService.getCinemaEntityById(4L)).thenReturn(cinema);
        when(showtimeRepository.existsByHallId(9L)).thenReturn(false);
        when(hallRepository.save(any(Hall.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseMessage<HallResponseDTO> result = hallService.updateHall(9L, request);

        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getObject().getName()).isEqualTo("Salon 1 Updated");
        assertThat(result.getObject().getRows()).isEqualTo(3);
        assertThat(result.getObject().getSeatsPerRow()).isEqualTo(4);
        assertThat(result.getObject().getCapacity()).isEqualTo(12);
        verify(seatRepository).deleteByHallId(9L);
    }

    @Test
    void getHallsByCinemaIdReturnsCinemaHalls() {
        Cinema cinema = Cinema.builder().id(4L).name("CineTime Kadikoy").build();
        Hall standardHall = Hall.builder()
                .id(1L)
                .name("Salon 1")
                .hallType(HallType.STANDARD)
                .rows(5)
                .seatsPerRow(8)
                .cinema(cinema)
                .build();
        Hall imaxHall = Hall.builder()
                .id(2L)
                .name("IMAX Salon")
                .hallType(HallType.IMAX)
                .rows(6)
                .seatsPerRow(10)
                .cinema(cinema)
                .build();

        when(cinemaService.getCinemaEntityById(4L)).thenReturn(cinema);
        when(hallRepository.findByCinema_IdOrderByNameAsc(4L)).thenReturn(List.of(imaxHall, standardHall));

        ResponseMessage<List<HallResponseDTO>> result = hallService.getHallsByCinemaId(4L);

        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getObject()).hasSize(2);
        assertThat(result.getObject().get(0).getName()).isEqualTo("IMAX Salon");
        assertThat(result.getObject().get(0).getHallType()).isEqualTo(HallType.IMAX);
        assertThat(result.getObject().get(0).getRows()).isEqualTo(6);
        assertThat(result.getObject().get(0).getSeatsPerRow()).isEqualTo(10);
        assertThat(result.getObject().get(0).getCapacity()).isEqualTo(60);
        assertThat(result.getObject().get(1).getName()).isEqualTo("Salon 1");
        assertThat(result.getObject().get(1).getHallType()).isEqualTo(HallType.STANDARD);
        assertThat(result.getObject().get(1).getRows()).isEqualTo(5);
        assertThat(result.getObject().get(1).getSeatsPerRow()).isEqualTo(8);
        assertThat(result.getObject().get(1).getCapacity()).isEqualTo(40);
    }
}