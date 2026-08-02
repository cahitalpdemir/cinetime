package com.tpe.cinetime.payload.request;


import com.tpe.cinetime.enums.MovieStatus;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
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
    @Positive(message = "Duration must be greater than zero")
    private Integer duration;

    @NotBlank
    private String director;

    @NotEmpty
    private List<String> cast;

    @NotEmpty
    private List<String> formats;

    @NotBlank
    private String genre;

    @Size(max = 500)
    @URL(message = "Poster URL must be a valid URL")
    @Pattern(regexp = "^https?://.+", message = "Poster URL must start with http:// or https://")
    private String posterUrl;

    private MovieStatus status = MovieStatus.COMING_SOON;

    private String specialHalls;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "10.0", message = "Rating must be at most 10")
    private Double rating;
}
