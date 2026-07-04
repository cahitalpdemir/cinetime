package com.tpe.cinetime.payload.response.booking;

import com.tpe.cinetime.enums.SeatType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {

    private Long id;
    private String ticketNumber;
    private String qrCode;
    private LocalDateTime createdAt;

    private Long bookingId;
    private Long showtimeId;
    private Long movieId;
    private String movieTitle;
    private String cinemaName;
    private String hallName;
    private LocalDate showtimeDate;
    private LocalTime startTime;

    private Long seatId;
    private String rowLetter;
    private Integer seatNumber;
    private SeatType seatType;
}
