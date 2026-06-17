package com.tpe.cinetime.payload.request.showtime;

import com.tpe.cinetime.constants.messages.ValidationMessages;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShowtimeRequest {

    @NotNull(message = ValidationMessages.SHOWTIME_MOVIE_ID_NOT_NULL)
    private Long movieId;

    @NotNull(message = ValidationMessages.SHOWTIME_HALL_ID_NOT_NULL)
    private Long hallId;

    //past date+time check is handled in service layer by combining date + startTime
    @NotNull(message = ValidationMessages.SHOWTIME_DATE_NOT_NULL)
    private LocalDate date;

    @NotNull(message = ValidationMessages.SHOWTIME_START_TIME_NOT_NULL)
    private LocalTime startTime;

    @NotBlank(message = ValidationMessages.SHOWTIME_LANGUAGE_NOT_BLANK)
    @Size(max = 50, message = ValidationMessages.SHOWTIME_LANGUAGE_SIZE)
    private String language;

    @NotBlank(message = ValidationMessages.SHOWTIME_FORMAT_NOT_BLANK)
    @Size(max = 20, message = ValidationMessages.SHOWTIME_FORMAT_SIZE)
    private String format;

    @NotNull(message = ValidationMessages.SHOWTIME_PRICE_NOT_NULL)
    @Positive(message = ValidationMessages.SHOWTIME_PRICE_POSITIVE)
    private Double price;
}
