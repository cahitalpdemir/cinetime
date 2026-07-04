package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.Seat;
import com.tpe.cinetime.entity.Showtime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketQrServiceTest {

    private final TicketQrService ticketQrService = new TicketQrService(
            "test-ticket-qr-secret-test-ticket-qr-secret");

    @Test
    void createsSignedPayloadWithExpectedClaims() {
        Showtime showtime = Showtime.builder()
                .id(20L)
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(22, 0))
                .build();
        Booking booking = Booking.builder().id(10L).showtime(showtime).build();
        Seat seat = Seat.builder().id(30L).build();

        String qrCode = ticketQrService.generateQrCode("TKT-TEST", booking, seat);
        Claims claims = ticketQrService.parseAndVerify(qrCode);

        assertEquals("TKT-TEST", claims.getSubject());
        assertEquals(10L, ((Number) claims.get("bookingId")).longValue());
        assertEquals(20L, ((Number) claims.get("showtimeId")).longValue());
        assertEquals(30L, ((Number) claims.get("seatId")).longValue());
    }

    @Test
    void rejectsTamperedPayload() {
        Showtime showtime = Showtime.builder()
                .id(20L)
                .date(LocalDate.now().plusDays(2))
                .startTime(LocalTime.of(20, 0))
                .endTime(LocalTime.of(22, 0))
                .build();
        Booking booking = Booking.builder().id(10L).showtime(showtime).build();
        Seat seat = Seat.builder().id(30L).build();
        String qrCode = ticketQrService.generateQrCode("TKT-TEST", booking, seat);

        int payloadIndex = qrCode.indexOf('.') + 2;
        char replacement = qrCode.charAt(payloadIndex) == 'a' ? 'b' : 'a';
        String tampered = qrCode.substring(0, payloadIndex)
                + replacement
                + qrCode.substring(payloadIndex + 1);

        assertThrows(JwtException.class, () -> ticketQrService.parseAndVerify(tampered));
    }
}
