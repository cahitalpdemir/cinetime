package com.tpe.cinetime.controller;

import com.tpe.cinetime.payload.request.SeatLockRequest;
import com.tpe.cinetime.payload.request.SeatUnlockRequest;
import com.tpe.cinetime.payload.response.SeatLockResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.security.UserDetailsImpl;
import com.tpe.cinetime.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/customer/seats")
@RequiredArgsConstructor
public class SeatLockController {

    private final SeatLockService seatLockService;


    @PostMapping("/lock")
    public ResponseEntity<ResponseMessage<SeatLockResponse>> lockSeat(
            @Valid @RequestBody SeatLockRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        ResponseMessage<SeatLockResponse> response = seatLockService.lockOrExtendSeat(
                request.getShowtimeId(), request.getSeatId(), request.getLockToken());

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping("/unlock")
    public ResponseEntity<ResponseMessage<?>> unlockSeat(
            @Valid @RequestBody SeatUnlockRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        ResponseMessage<?> response = seatLockService.unlockSingleSeat(
                request.getShowtimeId(), request.getSeatId(), request.getLockToken());

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}


