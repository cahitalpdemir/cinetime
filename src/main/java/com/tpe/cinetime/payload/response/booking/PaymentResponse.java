package com.tpe.cinetime.payload.response.booking;

import com.tpe.cinetime.enums.PaymentMethod;
import com.tpe.cinetime.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Double amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;
}
