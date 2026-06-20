package com.tpe.cinetime.controller.customer;

import com.tpe.cinetime.payload.request.booking.BookingRequest;
import com.tpe.cinetime.payload.request.booking.PaymentRequest;
import com.tpe.cinetime.payload.response.booking.BookingResponse;
import com.tpe.cinetime.payload.response.booking.PaymentResponse;
import com.tpe.cinetime.payload.response.booking.TicketResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.booking.BookingService;
import com.tpe.cinetime.service.booking.PaymentService;
import com.tpe.cinetime.service.booking.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final TicketService ticketService;

    @PostMapping("/bookings")
    public ResponseEntity<ResponseMessage<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(201).body(bookingService.createBooking(request));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ResponseMessage<List<BookingResponse>>> getUserBookings() {
        return ResponseEntity.ok(bookingService.getUserBookings());
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ResponseMessage<BookingResponse>> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<ResponseMessage<BookingResponse>> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PostMapping("/bookings/{id}/payment")
    public ResponseEntity<ResponseMessage<PaymentResponse>> processPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(id, request));
    }

    @GetMapping("/bookings/{id}/tickets")
    public ResponseEntity<ResponseMessage<List<TicketResponse>>> getTicketsByBooking(
            @PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketsByBookingId(id));
    }

    @GetMapping("/tickets/{ticketNumber}")
    public ResponseEntity<ResponseMessage<TicketResponse>> getTicketByNumber(
            @PathVariable String ticketNumber) {
        return ResponseEntity.ok(ticketService.getTicketByNumber(ticketNumber));
    }
}
