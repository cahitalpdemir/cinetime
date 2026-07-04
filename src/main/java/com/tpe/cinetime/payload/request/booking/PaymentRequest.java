package com.tpe.cinetime.payload.request.booking;

import com.tpe.cinetime.constants.messages.BookingValidationMessages;
import com.tpe.cinetime.enums.PaymentMethod;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class PaymentRequest {

    @NotNull(message = BookingValidationMessages.PAYMENT_METHOD_NOT_NULL)
    private PaymentMethod paymentMethod;

    @NotBlank(message = BookingValidationMessages.CARD_NUMBER_NOT_BLANK)
    @Pattern(regexp = "^\\d{16}$", message = BookingValidationMessages.CARD_NUMBER_INVALID)
    private String cardNumber;

    @NotBlank(message = BookingValidationMessages.CARD_HOLDER_NOT_BLANK)
    private String cardHolderName;
}
