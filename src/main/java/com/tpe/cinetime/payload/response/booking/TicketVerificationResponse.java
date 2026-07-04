package com.tpe.cinetime.payload.response.booking;

import com.tpe.cinetime.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVerificationResponse {

    private boolean valid;
    private String ticketNumber;
    private TicketStatus status;
    private String movieTitle;
    private String cinemaName;
    private String hallName;
    private LocalDate showtimeDate;
    private LocalTime startTime;
    private String rowLetter;
    private Integer seatNumber;
}
