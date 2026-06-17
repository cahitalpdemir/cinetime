package com.tpe.cinetime.payload.response.showtime;

import com.tpe.cinetime.enums.SeatType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAvailabilityResponse {

    private Long seatId;
    private String rowLetter;
    private Integer seatNumber;
    private SeatType seatType;

    //truee if seat is already booked for this showtime
    private Boolean isBooked;
}
