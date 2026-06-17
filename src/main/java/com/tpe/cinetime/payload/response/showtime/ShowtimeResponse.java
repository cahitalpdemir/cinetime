package com.tpe.cinetime.payload.response.showtime;

import com.tpe.cinetime.enums.ShowtimeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeResponse {

    private Long id;

    //movie fields
    private Long movieId;
    private String movieTitle;
    private Integer movieDuration;
    private String movieGenre;

    // hall and cinema fields
    private Long hallId;
    private String hallName;
    private String cinemaName;
    private String cinemaCity;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String language;
    private String format;
    private Double price;
    private ShowtimeStatus status;
    private LocalDateTime createdAt;
}
