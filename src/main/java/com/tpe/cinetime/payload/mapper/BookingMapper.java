package com.tpe.cinetime.payload.mapper;

import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.BookingSeat;
import com.tpe.cinetime.entity.Payment;
import com.tpe.cinetime.entity.Ticket;
import com.tpe.cinetime.payload.response.booking.BookedSeatResponse;
import com.tpe.cinetime.payload.response.booking.BookingResponse;
import com.tpe.cinetime.payload.response.booking.PaymentResponse;
import com.tpe.cinetime.payload.response.booking.TicketResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .showtimeId(booking.getShowtime().getId())
                .movieId(booking.getShowtime().getMovie().getId())
                .movieTitle(booking.getShowtime().getMovie().getTitle())
                .cinemaName(booking.getShowtime().getHall().getCinema().getName())
                .hallName(booking.getShowtime().getHall().getName())
                .showtimeDate(booking.getShowtime().getDate())
                .startTime(booking.getShowtime().getStartTime())
                .seats(toBookedSeatResponses(booking.getBookingSeats()))
                .payment(booking.getPayment() != null ? toPaymentResponse(booking.getPayment()) : null)
                .build();
    }

    public List<BookedSeatResponse> toBookedSeatResponses(List<BookingSeat> bookingSeats) {
        return bookingSeats.stream()
                .map(this::toBookedSeatResponse)
                .collect(Collectors.toList());
    }

    public BookedSeatResponse toBookedSeatResponse(BookingSeat bookingSeat) {
        return BookedSeatResponse.builder()
                .seatId(bookingSeat.getSeat().getId())
                .rowLetter(bookingSeat.getSeat().getRowLetter())
                .seatNumber(bookingSeat.getSeat().getSeatNumber())
                .seatType(bookingSeat.getSeat().getSeatType())
                .price(bookingSeat.getPrice())
                .build();
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .refundedAt(payment.getRefundedAt())
                .build();
    }

    public TicketResponse toTicketResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .qrCode(ticket.getQrCode())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .bookingId(ticket.getBooking().getId())
                .showtimeId(ticket.getBooking().getShowtime().getId())
                .movieId(ticket.getBooking().getShowtime().getMovie().getId())
                .movieTitle(ticket.getBooking().getShowtime().getMovie().getTitle())
                .cinemaName(ticket.getBooking().getShowtime().getHall().getCinema().getName())
                .hallName(ticket.getBooking().getShowtime().getHall().getName())
                .showtimeDate(ticket.getBooking().getShowtime().getDate())
                .startTime(ticket.getBooking().getShowtime().getStartTime())
                .seatId(ticket.getSeat().getId())
                .rowLetter(ticket.getSeat().getRowLetter())
                .seatNumber(ticket.getSeat().getSeatNumber())
                .seatType(ticket.getSeat().getSeatType())
                .build();
    }
}
