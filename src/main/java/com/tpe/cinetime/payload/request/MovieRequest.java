package com.tpe.cinetime.payload.request;


import com.tpe.cinetime.enums.MovieStatus;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    @Size(min = 3, max = 300)
    private String summary;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    private Integer duration;

    @NotBlank
    private String director;

    @NotEmpty
    private List<String> cast;

    @NotEmpty
    private List<String> formats;

    @NotBlank
    private String genre;

    private MovieStatus status = MovieStatus.COMING_SOON;

    private String specialHalls;

    private Double rating;
}
