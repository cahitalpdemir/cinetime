package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.constants.messages.BookingErrorMessages;
import com.tpe.cinetime.constants.messages.BookingSuccessMessages;
import com.tpe.cinetime.entity.*;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.enums.ShowtimeStatus;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.mapper.BookingMapper;
import com.tpe.cinetime.payload.request.booking.BookingRequest;
import com.tpe.cinetime.payload.response.booking.BookingResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.booking.BookingRepository;
import com.tpe.cinetime.repository.booking.BookingSeatRepository;
import com.tpe.cinetime.repository.cinema.SeatRepository;
import com.tpe.cinetime.repository.showtime.ShowtimeRepository;
import com.tpe.cinetime.service.helpers.MethodHelper;
import com.tpe.cinetime.service.showtime.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeService showtimeService;
    private final BookingMapper bookingMapper;
    private final MethodHelper methodHelper;

    @Transactional
    public ResponseMessage<BookingResponse> createBooking(BookingRequest request) {
        User user = methodHelper.currentUser();
        Showtime showtime = showtimeService.getShowtimeById(request.getShowtimeId());

        showtimeService.validateShowtimeForBooking(showtime.getId());
        validateShowtimeNotInPast(showtime);
        validateSeatSelection(request.getSeatIds());

        Set<Long> alreadyBookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeId(
                showtime.getId(), ACTIVE_BOOKING_STATUSES);

        List<Seat> selectedSeats = resolveAndValidateSeats(
                request.getSeatIds(), showtime, alreadyBookedSeatIds);

        double seatPrice = showtime.getPrice();
        double totalPrice = seatPrice * selectedSeats.size();

        Booking booking = Booking.builder()
                .user(user)
                .showtime(showtime)
                .status(BookingStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        List<BookingSeat> bookingSeats = selectedSeats.stream()
                .map(seat -> BookingSeat.builder()
                        .booking(booking)
                        .seat(seat)
                        .showtime(showtime)
                        .price(seatPrice)
                        .build())
                .collect(Collectors.toList());

        booking.setBookingSeats(bookingSeats);

        Booking saved = bookingRepository.save(booking);

        return ResponseMessage.<BookingResponse>builder()
                .object(bookingMapper.toResponse(saved))
                .message(BookingSuccessMessages.BOOKING_CREATED_SUCCESSFULLY)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ResponseMessage<List<BookingResponse>> getUserBookings() {
        User user = methodHelper.currentUser();

        List<BookingResponse> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseMessage.<List<BookingResponse>>builder()
                .object(bookings)
                .message(BookingSuccessMessages.BOOKINGS_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<BookingResponse> getBookingById(Long id) {
        Booking booking = getBookingForCurrentUser(id);

        return ResponseMessage.<BookingResponse>builder()
                .object(bookingMapper.toResponse(booking))
                .message(BookingSuccessMessages.BOOKING_FETCHED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ResponseMessage<BookingResponse> cancelBooking(Long id) {
        Booking booking = getBookingForCurrentUser(id);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_ALREADY_CANCELLED);
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_CANNOT_CANCEL_CONFIRMED);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        return ResponseMessage.<BookingResponse>builder()
                .object(bookingMapper.toResponse(saved))
                .message(BookingSuccessMessages.BOOKING_CANCELLED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public Booking getBookingForCurrentUser(Long id) {
        User user = methodHelper.currentUser();

        return bookingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(BookingErrorMessages.BOOKING_NOT_FOUND, id)));
    }

    public void updateShowtimeSoldOutStatus(Long showtimeId) {
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);
        List<Seat> allSeats = seatRepository.findByHallId(showtime.getHall().getId());

        long bookedCount = bookingSeatRepository.countByShowtimeIdAndBooking_StatusIn(
                showtimeId, ACTIVE_BOOKING_STATUSES);

        if (bookedCount >= allSeats.size()) {
            showtime.setStatus(ShowtimeStatus.SOLD_OUT);
            showtimeRepository.save(showtime);
        }
    }

    private void validateShowtimeNotInPast(Showtime showtime) {
        LocalDateTime showtimeDateTime = LocalDateTime.of(showtime.getDate(), showtime.getStartTime());
        if (showtimeDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_SHOWTIME_IN_PAST);
        }
    }

    private void validateSeatSelection(List<Long> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_SEATS_EMPTY);
        }

        if (seatIds.size() != new HashSet<>(seatIds).size()) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_SEAT_ALREADY_TAKEN);
        }
    }

    private List<Seat> resolveAndValidateSeats(
            List<Long> seatIds,
            Showtime showtime,
            Set<Long> alreadyBookedSeatIds) {

        List<Seat> selectedSeats = new ArrayList<>();

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format(BookingErrorMessages.BOOKING_SEAT_NOT_FOUND, seatId)));

            if (!seat.getHall().getId().equals(showtime.getHall().getId())) {
                throw new BadRequestException(BookingErrorMessages.BOOKING_SEAT_NOT_IN_HALL);
            }

            if (alreadyBookedSeatIds.contains(seatId)) {
                throw new BadRequestException(BookingErrorMessages.BOOKING_SEAT_ALREADY_TAKEN);
            }

            selectedSeats.add(seat);
        }

        return selectedSeats;
    }
}
