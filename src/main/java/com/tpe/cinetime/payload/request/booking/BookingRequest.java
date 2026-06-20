package com.tpe.cinetime.payload.request.booking;

import com.tpe.cinetime.constants.messages.BookingValidationMessages;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = BookingValidationMessages.SHOWTIME_ID_NOT_NULL)
    private Long showtimeId;

    @NotEmpty(message = BookingValidationMessages.SEAT_IDS_NOT_EMPTY)
    private List<Long> seatIds;
}
