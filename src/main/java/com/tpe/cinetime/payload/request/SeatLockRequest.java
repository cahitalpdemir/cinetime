package com.tpe.cinetime.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SeatLockRequest {
    @NotNull(message = "Seans id boş olamaz")
    private Long showtimeId;

    @NotNull(message = "Koltuk id boş olamaz")
    private Long seatId;

    // İLK koltuk seçimindeyse frontend bu alanı göndermez (null gelir).
    // İkinci ve sonraki koltuklarda frontend, önceki cevaptan aldığı token'ı buraya koyar.
    private String lockToken;
}
