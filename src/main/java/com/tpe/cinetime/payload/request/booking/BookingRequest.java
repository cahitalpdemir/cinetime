package com.tpe.cinetime.payload.request.booking;

import com.tpe.cinetime.constants.messages.BookingValidationMessages;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = BookingValidationMessages.SHOWTIME_ID_NOT_NULL)
    @Positive(message = "Showtime id must be greater than zero")
    private Long showtimeId;

    @NotEmpty(message = BookingValidationMessages.SEAT_IDS_NOT_EMPTY)
    @Size(max = 10, message = "At most 10 seats can be selected in one booking")
    private List<@NotNull(message = "Seat id cannot be null") @Positive(message = "Seat id must be greater than zero") Long> seatIds;

    // YENİ EKLENEN — koltuk seçimi sırasında alınan Redis kilidinin token'ı.
    // Booking oluşturulmadan önce, gönderilen seatIds'in gerçekten bu token'a
    // ait olup olmadığını doğrulayacağız.
    @NotBlank(message = "Lock token cannot be blank")
    private String lockToken;
}
