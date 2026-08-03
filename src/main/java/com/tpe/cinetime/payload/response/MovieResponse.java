package com.tpe.cinetime.payload.response;

import com.tpe.cinetime.enums.MovieStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private LocalDate releaseDate;
    private Integer duration;
    private Double rating;
    private String director;
    private List<String> cast;
    private List<String> formats;
    private String genre;
    private Long posterId;
    private String posterUrl;
    private String trailerUrl;
    private MovieStatus status;
    private String specialHalls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
