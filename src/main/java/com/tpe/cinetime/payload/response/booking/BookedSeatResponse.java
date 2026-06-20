package com.tpe.cinetime.payload.response.booking;

import com.tpe.cinetime.enums.SeatType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedSeatResponse {

    private Long seatId;
    private String rowLetter;
    private Integer seatNumber;
    private SeatType seatType;
    private Double price;
}
