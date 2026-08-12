package com.tpe.cinetime.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class SeatUnlockRequest {
    @NotNull(message = "Seans id boş olamaz")
    private Long showtimeId;

    @NotNull(message = "Koltuk listesi boş olamaz")
    private Long seatId;

    @NotBlank(message = "Lock token boş olamaz")
    private String lockToken;
}
