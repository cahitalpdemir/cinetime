package com.tpe.cinetime.payload.mapper;

import com.tpe.cinetime.entity.Showtime;
import com.tpe.cinetime.payload.response.showtime.ShowtimeResponse;
import org.springframework.stereotype.Component;

@Component
public class ShowtimeMapper {

    //converts showtime entity to response dto
    public ShowtimeResponse toResponse(Showtime showtime) {
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .movieTitle(showtime.getMovie().getTitle())
                .movieDuration(showtime.getMovie().getDuration())
                .movieGenre(showtime.getMovie().getGenre())
                .hallId(showtime.getHall().getId())
                .hallName(showtime.getHall().getName())
                .cinemaName(showtime.getHall().getCinema().getName())
                .cinemaCity(showtime.getHall().getCinema().getCity())
                .date(showtime.getDate())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .language(showtime.getLanguage())
                .format(showtime.getFormat())
                .price(showtime.getPrice())
                .status(showtime.getStatus())
                .createdAt(showtime.getCreatedAt())
                .build();
    }
}
