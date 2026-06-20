package com.tpe.cinetime.payload.response.booking;

import com.tpe.cinetime.enums.BookingStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private BookingStatus status;
    private Double totalPrice;
    private LocalDateTime createdAt;

    private Long showtimeId;
    private Long movieId;
    private String movieTitle;
    private String cinemaName;
    private String hallName;
    private LocalDate showtimeDate;
    private LocalTime startTime;

    private List<BookedSeatResponse> seats;
    private PaymentResponse payment;
}
