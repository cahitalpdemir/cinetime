package com.tpe.cinetime.payload.request.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketVerificationRequest {

    @NotBlank(message = "QR code is required")
    private String qrCode;
}
