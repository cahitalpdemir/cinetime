package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.constants.messages.BookingErrorMessages;
import com.tpe.cinetime.constants.messages.BookingSuccessMessages;
import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.Payment;
import com.tpe.cinetime.enums.BookingStatus;
import com.tpe.cinetime.enums.PaymentStatus;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.payload.mapper.BookingMapper;
import com.tpe.cinetime.payload.request.booking.PaymentRequest;
import com.tpe.cinetime.payload.response.booking.PaymentResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.repository.booking.PaymentRepository;
import com.tpe.cinetime.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final TicketService ticketService;
    private final BookingMapper bookingMapper;
    private final SeatLockService seatLockService; // YENİ EKLENEN dependency


    @Transactional
    public ResponseMessage<PaymentResponse> processPayment(Long bookingId, PaymentRequest request) {
        Booking booking = bookingService.getBookingForCurrentUserForUpdate(bookingId);

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_ALREADY_CONFIRMED);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException(BookingErrorMessages.BOOKING_ALREADY_CANCELLED);
        }

        paymentRepository.findByBookingId(bookingId).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.COMPLETED) {
                throw new BadRequestException(BookingErrorMessages.PAYMENT_ALREADY_COMPLETED);
            }
        });

        if (!processMockPayment(request)) {
            throw new BadRequestException(BookingErrorMessages.PAYMENT_FAILED);
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(request.getPaymentMethod())
                .transactionId(generateTransactionId())
                .build();

        booking.setPayment(payment);
        booking.setStatus(BookingStatus.CONFIRMED);

        Payment saved = paymentRepository.save(payment);
        ticketService.generateTicketsForBooking(booking);
        bookingService.updateShowtimeSoldOutStatus(booking.getShowtime().getId());

        // YENİ EKLENEN — ödeme başarılı, artık Redis kilidine gerek yok, serbest bırakıyoruz
        if (booking.getLockToken() != null) {
            List<Long> seatIds = booking.getBookingSeats().stream()
                    .map(bs -> bs.getSeat().getId())
                    .toList();
            seatLockService.releaseAllSeatsForToken(
                    booking.getShowtime().getId(), seatIds, booking.getLockToken());
        }

        return ResponseMessage.<PaymentResponse>builder()
                .object(bookingMapper.toPaymentResponse(saved))
                .message(BookingSuccessMessages.PAYMENT_COMPLETED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private boolean processMockPayment(PaymentRequest request) {
        return request.getCardNumber() != null
                && request.getCardNumber().matches("^\\d{16}$")
                && !request.getCardNumber().endsWith("0000");
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
