package com.tpe.cinetime.controller;

import com.tpe.cinetime.enums.MovieStatus;
import com.tpe.cinetime.payload.response.MovieResponse;
import com.tpe.cinetime.payload.response.PageResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.MovieService;
import com.tpe.cinetime.service.showtime.ShowtimeService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MovieControllerTest {

    private final MovieService movieService = mock(MovieService.class);
    private final ShowtimeService showtimeService = mock(ShowtimeService.class);
    private final MovieController controller = new MovieController(movieService, showtimeService);

    @Test
    void getAllMoviesReturnsStandardResponseAndCompactPage() {
        MovieResponse movie = MovieResponse.builder()
                .id(1L)
                .title("Dune Part Two")
                .status(MovieStatus.NOW_SHOWING)
                .build();
        when(movieService.getAllMovies(isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(movie)));

        ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> result =
                controller.getAllMovies(null, 0, 10, "title", "asc");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getObject().getContent()).containsExactly(movie);
        assertThat(result.getBody().getObject().getTotalElements()).isEqualTo(1);
    }
}
