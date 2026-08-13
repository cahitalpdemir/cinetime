package com.tpe.cinetime.service.showtime;

import com.tpe.cinetime.entity.Hall;
import com.tpe.cinetime.entity.Seat;
import com.tpe.cinetime.entity.Showtime;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.payload.response.showtime.SeatAvailabilityResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.MovieRepository;
import com.tpe.cinetime.repository.booking.BookingSeatRepository;
import com.tpe.cinetime.repository.cinema.HallRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import com.tpe.cinetime.repository.showtime.ShowtimeRepository;
import com.tpe.cinetime.payload.mapper.ShowtimeMapper;
import com.tpe.cinetime.service.SeatLockService;
import com.tpe.cinetime.service.booking.BookingCancellationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowtimeSeatAvailabilityTest {

    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private ShowtimeMapper showtimeMapper;
    @Mock
    private BookingSeatRepository bookingSeatRepository;
    @Mock
    private BookingCancellationService bookingCancellationService;
    @Mock
    private SeatLockService seatLockService; // YENİ EKLENEN

    @InjectMocks
    private ShowtimeService showtimeService;

    @Test
    void returnsBookedAndAvailableSeatsFromActiveBookings() {
        Hall hall = Hall.builder().id(5L).build();
        Showtime showtime = Showtime.builder().id(10L).hall(hall).build();
        Seat bookedSeat = Seat.builder().id(100L).hall(hall).build();
        Seat availableSeat = Seat.builder().id(101L).hall(hall).build();

        when(showtimeRepository.findById(10L)).thenReturn(Optional.of(showtime));
        when(seatRepository.findByHallId(5L)).thenReturn(List.of(bookedSeat, availableSeat));
        when(bookingSeatRepository.findBookedSeatIdsByShowtimeId(
                10L, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(Set.of(100L));

        // YENİ EKLENEN — findLockedSeatIds artık ShowtimeService içinde çağrılıyor,
        // NPE almamak için boş bir Set dönmesini sağlıyoruz (bu testte kilit senaryosu yok)
        when(seatLockService.findLockedSeatIds(10L, List.of(100L, 101L)))
                .thenReturn(Set.of());

        ResponseMessage<List<SeatAvailabilityResponse>> response =
                showtimeService.getShowtimeSeats(10L);

        assertTrue(response.getObject().get(0).getIsBooked());
        assertFalse(response.getObject().get(1).getIsBooked());
    }
}