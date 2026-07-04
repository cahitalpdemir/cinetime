package com.tpe.cinetime.controller.admin;

import com.tpe.cinetime.payload.request.booking.TicketVerificationRequest;
import com.tpe.cinetime.payload.response.booking.TicketVerificationResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.booking.TicketService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {

    private final TicketService ticketService;

    @PostMapping("/verify")
    public ResponseEntity<ResponseMessage<TicketVerificationResponse>> verifyTicket(
            @Valid @RequestBody TicketVerificationRequest request) {
        return ResponseEntity.ok(ticketService.verifyTicket(request.getQrCode()));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ResponseMessage<TicketVerificationResponse>> checkInTicket(
            @Valid @RequestBody TicketVerificationRequest request) {
        return ResponseEntity.ok(ticketService.checkInTicket(request.getQrCode()));
    }
}
