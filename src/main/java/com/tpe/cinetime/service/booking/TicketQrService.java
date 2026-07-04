package com.tpe.cinetime.service.booking;

import com.tpe.cinetime.entity.Booking;
import com.tpe.cinetime.entity.Seat;
import com.tpe.cinetime.entity.Showtime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TicketQrService {

    private final SecretKey signingKey;

    public TicketQrService(@Value("${ticket.qr.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateQrCode(String ticketNumber, Booking booking, Seat seat) {
        Showtime showtime = booking.getShowtime();
        Date expiration = Date.from(showtimeExpiration(showtime)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        return Jwts.builder()
                .setSubject(ticketNumber)
                .claim("bookingId", booking.getId())
                .claim("showtimeId", showtime.getId())
                .claim("seatId", seat.getId())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public Claims parseAndVerify(String qrCode) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(qrCode)
                .getBody();
    }

    private LocalDateTime showtimeExpiration(Showtime showtime) {
        LocalDate endDate = showtime.getDate();
        if (!showtime.getEndTime().isAfter(showtime.getStartTime())) {
            endDate = endDate.plusDays(1);
        }
        return LocalDateTime.of(endDate, showtime.getEndTime()).plusHours(6);
    }
}
