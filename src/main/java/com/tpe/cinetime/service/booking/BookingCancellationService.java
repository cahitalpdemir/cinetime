package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.constants.messages.BookingErrorMessages;
import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.Payment;
import com.tpe.cinetime.entity.Showtime;
import com.tpe.cinetime.entity.Ticket;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.enums.PaymentStatus;
import com.tpe.cinetime.enums.TicketStatus;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.repository.booking.BookingRepository;
import com.tpe.cinetime.repository.booking.PaymentRepository;
import com.tpe.cinetime.repository.booking.TicketRepository;
import com.tpe.cinetime.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingCancellationService {

    private static final List<BookingStatus> CANCELLABLE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final SeatLockService seatLockService;

    @Value("${app.booking.cancellation-cutoff-minutes:120}")
    private long cancellationCutoffMinutes;

    @Transactional
    public boolean cancelByCustomer(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_ALREADY_CANCELLED);
        }

        boolean refunded = booking.getStatus() == BookingStatus.CONFIRMED;
        if (refunded) {
            validateCancellationWindow(booking.getShowtime());
            refundAndCancelTickets(booking);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return refunded;
    }

    @Transactional
    public void cancelForShowtime(Showtime showtime) {
        bookingRepository.findByShowtimeIdAndStatusIn(showtime.getId(), CANCELLABLE_STATUSES)
                .forEach(booking -> {
                    if (booking.getStatus() == BookingStatus.CONFIRMED) {
                        refundAndCancelTickets(booking);
                    }
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                });
    }

    /**
     * Booking'e ait Redis kilidi varsa serbest bırakır.
     * lockToken null ise (örn. CONFIRMED bir booking'de zaten PaymentService
     * ödeme anında kilidi silmişti) hiçbir şey yapmaz.
     */
    private void releaseSeatLockIfPresent(Booking booking) {
        if (booking.getLockToken() == null) {
            return;
        }

        List<Long> seatIds = booking.getBookingSeats().stream()
                .map(bs -> bs.getSeat().getId())
                .toList();

        seatLockService.releaseAllSeatsForToken(
                booking.getShowtime().getId(), seatIds, booking.getLockToken());
    }

    private void validateCancellationWindow(Showtime showtime) {
        LocalDateTime showtimeStart = LocalDateTime.of(showtime.getDate(), showtime.getStartTime());
        LocalDateTime deadline = showtimeStart.minusMinutes(cancellationCutoffMinutes);
        if (!LocalDateTime.now().isBefore(deadline)) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_CANCELLATION_WINDOW_CLOSED);
        }
    }

    private void refundAndCancelTickets(Booking booking) {
        List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
        if (tickets.stream().anyMatch(ticket -> ticket.getStatus() == TicketStatus.USED)) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_HAS_USED_TICKET);
        }

        Payment payment = paymentRepository.findByBookingId(booking.getId())
                .filter(existing -> existing.getStatus() == PaymentStatus.COMPLETED)
                .orElseThrow(() -> new BadRequestException(
                        BookingErrorMessages.PAYMENT_REFUND_NOT_AVAILABLE));

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        tickets.forEach(ticket -> ticket.setStatus(TicketStatus.CANCELLED));
        ticketRepository.saveAll(tickets);
    }
}
