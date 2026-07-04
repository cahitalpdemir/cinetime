package com.tpe.cinetime.service.booking;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingCancellationServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private BookingCancellationService cancellationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cancellationService, "cancellationCutoffMinutes", 120L);
    }

    @Test
    void confirmedBookingIsRefundedAndTicketsAreCancelled() {
        Booking booking = confirmedBooking(LocalDate.now().plusDays(2));
        Payment payment = Payment.builder().status(PaymentStatus.COMPLETED).build();
        Ticket ticket = Ticket.builder().status(TicketStatus.ACTIVE).build();
        when(paymentRepository.findByBookingId(booking.getId())).thenReturn(Optional.of(payment));
        when(ticketRepository.findByBookingId(booking.getId())).thenReturn(List.of(ticket));

        boolean refunded = cancellationService.cancelByCustomer(booking);

        assertTrue(refunded);
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
        assertNotNull(payment.getRefundedAt());
        assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
    }

    @Test
    void confirmedBookingCannotBeCancelledAfterCutoff() {
        Booking booking = confirmedBooking(LocalDate.now());
        booking.getShowtime().setStartTime(LocalTime.now().plusMinutes(30));

        assertThrows(BadRequestException.class,
                () -> cancellationService.cancelByCustomer(booking));
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    void showtimeCancellationRefundsWithoutCustomerCutoff() {
        Booking booking = confirmedBooking(LocalDate.now());
        Payment payment = Payment.builder().status(PaymentStatus.COMPLETED).build();
        Ticket ticket = Ticket.builder().status(TicketStatus.ACTIVE).build();
        when(bookingRepository.findByShowtimeIdAndStatusIn(
                booking.getShowtime().getId(),
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(List.of(booking));
        when(paymentRepository.findByBookingId(booking.getId())).thenReturn(Optional.of(payment));
        when(ticketRepository.findByBookingId(booking.getId())).thenReturn(List.of(ticket));

        cancellationService.cancelForShowtime(booking.getShowtime());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(PaymentStatus.REFUNDED, payment.getStatus());
        assertEquals(TicketStatus.CANCELLED, ticket.getStatus());
    }

    @Test
    void bookingWithUsedTicketCannotBeRefunded() {
        Booking booking = confirmedBooking(LocalDate.now().plusDays(2));
        Ticket usedTicket = Ticket.builder().status(TicketStatus.USED).build();
        when(ticketRepository.findByBookingId(booking.getId())).thenReturn(List.of(usedTicket));

        assertThrows(BadRequestException.class,
                () -> cancellationService.cancelByCustomer(booking));
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    }

    private Booking confirmedBooking(LocalDate date) {
        Showtime showtime = Showtime.builder()
                .id(40L)
                .date(date)
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(22, 0))
                .build();
        return Booking.builder()
                .id(50L)
                .showtime(showtime)
                .status(BookingStatus.CONFIRMED)
                .build();
    }
}
