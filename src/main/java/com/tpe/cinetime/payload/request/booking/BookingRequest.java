package com.tpe.cinetime.payload.request.booking;

import com.tpe.cinetime.constants.messages.BookingValidationMessages;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = BookingValidationMessages.SHOWTIME_ID_NOT_NULL)
    @Positive(message = "Showtime id must be greater than zero")
    private Long showtimeId;

    @NotEmpty(message = BookingValidationMessages.SEAT_IDS_NOT_EMPTY)
    @Size(max = 10, message = "At most 10 seats can be selected in one booking")
    private List<@NotNull(message = "Seat id cannot be null") @Positive(message = "Seat id must be greater than zero") Long> seatIds;
}
