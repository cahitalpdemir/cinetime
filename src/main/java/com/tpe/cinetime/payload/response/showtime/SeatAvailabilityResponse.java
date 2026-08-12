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

    // YENİ EKLENEN — true ise koltuk şu an başka bir kullanıcı tarafından
    // Redis'te geçici olarak kilitli (henüz booking'e dönüşmemiş, seçim aşamasında).
    // isBooked'dan farklı: bu durum TTL ile kendiliğinden geçebilir.
    private Boolean isLocked;
}
