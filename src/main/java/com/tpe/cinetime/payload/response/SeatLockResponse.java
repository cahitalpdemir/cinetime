package com.tpe.cinetime.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class SeatLockResponse {
    private String lockToken;
    private Long seatId;
    private Instant expiresAt;
}
