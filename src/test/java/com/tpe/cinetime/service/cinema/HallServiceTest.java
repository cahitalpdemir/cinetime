package com.tpe.cinetime.service.cinema;

import com.tpe.cinetime.entity.Cinema;
import com.tpe.cinetime.entity.Hall;
import com.tpe.cinetime.enums.HallType;
import com.tpe.cinetime.payload.request.cinema.HallRequestDTO;
import com.tpe.cinetime.payload.response.cinema.HallResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.cinema.HallRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HallServiceTest {

    private final HallRepository hallRepository = mock(HallRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final CinemaService cinemaService = mock(CinemaService.class);
    private final HallService hallService = new HallService(hallRepository, seatRepository, cinemaService);

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
        assertThat(result.getObject().getCinemaId()).isEqualTo(4L);
        assertThat(result.getObject().getCapacity()).isEqualTo(6);
        assertThat(result.getObject().getCreatedSeatCount()).isEqualTo(6);
    }
}
