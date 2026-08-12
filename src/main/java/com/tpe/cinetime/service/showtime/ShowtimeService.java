package com.tpe.cinetime.service.showtime;

import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.entity.Hall;
import com.tpe.cinetime.entity.Movie;
import com.tpe.cinetime.entity.Seat;
import com.tpe.cinetime.entity.Showtime;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.enums.ShowtimeStatus;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.mapper.ShowtimeMapper;
import com.tpe.cinetime.payload.request.showtime.ShowtimeRequest;
import com.tpe.cinetime.payload.response.showtime.SeatAvailabilityResponse;
import com.tpe.cinetime.payload.response.showtime.ShowtimeResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.MovieRepository;
import com.tpe.cinetime.repository.booking.BookingSeatRepository;
import com.tpe.cinetime.repository.cinema.HallRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import com.tpe.cinetime.repository.showtime.ShowtimeRepository;
import com.tpe.cinetime.service.SeatLockService;
import com.tpe.cinetime.service.booking.BookingCancellationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeService {

    private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeMapper showtimeMapper;
    private final BookingSeatRepository bookingSeatRepository;
    private final BookingCancellationService bookingCancellationService;

    private final SeatLockService seatLockService;

    //create a new showtime, validates past datetime and hall conflicts
    @Transactional
    public ResponseMessage<ShowtimeResponse> createShowtime(ShowtimeRequest request) {

// combine date and startTime-only checking date is not enough
        LocalDateTime requestedDateTime = LocalDateTime.of(request.getDate(), request.getStartTime());
        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ErrorMessages.SHOWTIME_DATE_IN_PAST);
        }

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.MOVIE_NOT_FOUND, request.getMovieId())));

        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.HALL_NOT_FOUND, request.getHallId())));

        //endTime is calculated from movie duration in minutes
        LocalTime endTime = request.getStartTime().plusMinutes(movie.getDuration());

        //prevent double booking for the same hall at the same time
        boolean hasConflict = showtimeRepository.existsConflictingShowtime(
                hall.getId(), request.getDate(), request.getStartTime(), endTime);
        if (hasConflict) {
            throw new BadRequestException(ErrorMessages.SHOWTIME_HALL_CONFLICT);
        }

        Showtime showtime = Showtime.builder()
                .movie(movie)
                .hall(hall)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .language(request.getLanguage())
                .format(request.getFormat())
                .price(request.getPrice())
                .build();

        Showtime saved = showtimeRepository.save(showtime);

        return ResponseMessage.<ShowtimeResponse>builder()
                .object(showtimeMapper.toResponse(saved))
                .message(SuccessMessages.SHOWTIME_CREATED_SUCCESSFULLY)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    //cancel a showtime â€” only active showtimes can be cancelled
    @Transactional
    public ResponseMessage<ShowtimeResponse> cancelShowtime(Long id) {
        Showtime showtime = showtimeRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.SHOWTIME_NOT_FOUND, id)));

        if (showtime.getStatus() == ShowtimeStatus.CANCELLED) {
            throw new BadRequestException(ErrorMessages.SHOWTIME_ALREADY_CANCELLED);
        }
        if (!LocalDateTime.now().isBefore(
                LocalDateTime.of(showtime.getDate(), showtime.getStartTime()))) {
            throw new BadRequestException(ErrorMessages.SHOWTIME_CANCELLATION_AFTER_START);
        }

        bookingCancellationService.cancelForShowtime(showtime);
        showtime.setStatus(ShowtimeStatus.CANCELLED);
        Showtime saved = showtimeRepository.save(showtime);

        return ResponseMessage.<ShowtimeResponse>builder()
                .object(showtimeMapper.toResponse(saved))
                .message(SuccessMessages.SHOWTIME_CANCELLED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //returns active showtimes filtered by optional movieId, hallId and date
    public ResponseMessage<List<ShowtimeResponse>> getShowtimes(Long movieId, Long hallId, LocalDate date) {
        List<ShowtimeResponse> responses = findActiveShowtimes(movieId, hallId, date)
                .stream()
                .map(showtimeMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseMessage.<List<ShowtimeResponse>>builder()
                .object(responses)
                .message(SuccessMessages.SHOWTIMES_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //returns active upcoming showtimes for a movie
    public List<ShowtimeResponse> getUpcomingShowtimesByMovieId(Long movieId) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.MOVIE_NOT_FOUND, movieId)));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return showtimeRepository.findByMovieIdAndStatus(movieId, ShowtimeStatus.ACTIVE)
                .stream()
                .filter(showtime -> showtime.getDate().isAfter(today)
                        || (showtime.getDate().isEqual(today) && !showtime.getStartTime().isBefore(now)))
                .map(showtimeMapper::toResponse)
                .collect(Collectors.toList());
    }

    private List<Showtime> findActiveShowtimes(Long movieId, Long hallId, LocalDate date) {
        ShowtimeStatus active = ShowtimeStatus.ACTIVE;

        if (movieId != null && hallId != null && date != null) {
            return showtimeRepository.findByMovieIdAndHallIdAndDateAndStatus(movieId, hallId, date, active);
        }
        if (movieId != null && hallId != null) {
            return showtimeRepository.findByMovieIdAndHallIdAndStatus(movieId, hallId, active);
        }
        if (movieId != null && date != null) {
            return showtimeRepository.findByMovieIdAndDateAndStatus(movieId, date, active);
        }
        if (hallId != null && date != null) {
            return showtimeRepository.findByHallIdAndDateAndStatus(hallId, date, active);
        }
        if (movieId != null) {
            return showtimeRepository.findByMovieIdAndStatus(movieId, active);
        }
        if (hallId != null) {
            return showtimeRepository.findByHallIdAndStatus(hallId, active);
        }
        if (date != null) {
            return showtimeRepository.findByDateAndStatus(date, active);
        }
        return showtimeRepository.findByStatus(active);
    }

    //returns all seats for a showtime with booked/available status
    public ResponseMessage<List<SeatAvailabilityResponse>> getShowtimeSeats(Long id) {
        Showtime showtime = getShowtimeById(id);

        List<Seat> allSeats = seatRepository.findByHallId(showtime.getHall().getId());
        Set<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeId(
                showtime.getId(), ACTIVE_BOOKING_STATUSES);

        // YENİ EKLENEN — Redis'te kilitli olan koltukları da sorguluyoruz
        List<Long> allSeatIds = allSeats.stream().map(Seat::getId).toList();
        Set<Long> lockedSeatIds = seatLockService.findLockedSeatIds(showtime.getId(), allSeatIds);

        List<SeatAvailabilityResponse> seatResponses = allSeats.stream()
           .map(seat -> SeatAvailabilityResponse.builder()
                        .seatId(seat.getId())
                        .rowLetter(seat.getRowLetter())
                        .seatNumber(seat.getSeatNumber())
                        .seatType(seat.getSeatType())
                        .isBooked(bookedSeatIds.contains(seat.getId()))
                        .isLocked(lockedSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());

        return ResponseMessage.<List<SeatAvailabilityResponse>>builder()
                .object(seatResponses)
                .message(SuccessMessages.SHOWTIME_SEATS_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    //called by booking service to validate showtime before booking
    public void validateShowtimeForBooking(Long showtimeId) {
        validateShowtimeForBooking(getShowtimeById(showtimeId));
    }

    public void validateShowtimeForBooking(Showtime showtime) {
        if (showtime.getStatus() == ShowtimeStatus.CANCELLED) {
            throw new BadRequestException(ErrorMessages.SHOWTIME_IS_CANCELLED);
        }
        if (showtime.getStatus() == ShowtimeStatus.SOLD_OUT) {
            throw new BadRequestException(ErrorMessages.SHOWTIME_IS_SOLD_OUT);
        }
    }

    //return showtime entity by id â€” used internally by other methods
    public Showtime getShowtimeById(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ErrorMessages.SHOWTIME_NOT_FOUND, id)));
    }
}
