package com.tpe.cinetime.controller;

import com.tpe.cinetime.constants.messages.SuccessMessages;
import com.tpe.cinetime.exception.BadRequestException;
import com.tpe.cinetime.payload.request.MovieRequest;
import com.tpe.cinetime.payload.response.MovieResponse;
import com.tpe.cinetime.payload.response.PageResponse;
import com.tpe.cinetime.payload.response.showtime.ShowtimeResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.MovieService;
import com.tpe.cinetime.service.showtime.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "title", "releaseDate", "duration", "rating", "createdAt", "updatedAt");

    private final MovieService movieService;
    private final ShowtimeService showtimeService;

    @GetMapping
    public ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> getAllMovies(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        return pageResponse(movieService.getAllMovies(q, pageable(page, size, sort, type)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseMessage<MovieResponse>> getMovieBySlug(@PathVariable String slug) {
        return ok(movieService.getMovieBySlug(slug), SuccessMessages.MOVIE_FETCHED_SUCCESSFULLY);
    }

    @GetMapping("/hall/{hall}")
    public ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> getMoviesByHall(
            @PathVariable String hall,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        return pageResponse(movieService.getMoviesBySpecialHall(hall, pageable(page, size, sort, type)));
    }

    @GetMapping("/now-showing")
    public ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> getMoviesNowShowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        return pageResponse(movieService.getMoviesInTheaters(pageable(page, size, sort, type)));
    }

    @Operation(hidden = true)
    @GetMapping("/in-theaters")
    public ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> getMoviesInTheaters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        return getMoviesNowShowing(page, size, sort, type);
    }

    @GetMapping("/coming-soon")
    public ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> getMoviesComingSoon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        return pageResponse(movieService.getMoviesComingSoon(pageable(page, size, sort, type)));
    }

    @GetMapping("/archived")
    public ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> getMoviesArchived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        return pageResponse(movieService.getMoviesArchived(pageable(page, size, sort, type)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<MovieResponse>> getMovieById(@PathVariable Long id) {
        return ok(movieService.getMovieById(id), SuccessMessages.MOVIE_FETCHED_SUCCESSFULLY);
    }

    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseMessage<MovieResponse>> getMovieByIdAdmin(@PathVariable Long id) {
        return ok(movieService.getMovieByIdAdmin(id), SuccessMessages.MOVIE_FETCHED_SUCCESSFULLY);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseMessage<MovieResponse>> createMovie(
            @Valid @RequestBody MovieRequest request) {
        ResponseMessage<MovieResponse> response = response(
                movieService.createMovie(request),
                SuccessMessages.MOVIE_CREATED_SUCCESSFULLY,
                HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseMessage<MovieResponse>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {
        return ok(movieService.updateMovie(id, request), SuccessMessages.MOVIE_UPDATED_SUCCESSFULLY);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseMessage<MovieResponse>> deleteMovie(@PathVariable Long id) {
        return ok(movieService.deleteMovie(id), SuccessMessages.MOVIE_DELETED_SUCCESSFULLY);
    }

    @GetMapping("/{id}/showtimes")
    public ResponseEntity<ResponseMessage<List<ShowtimeResponse>>> getMovieShowtimes(@PathVariable Long id) {
        return ok(
                showtimeService.getUpcomingShowtimesByMovieId(id),
                SuccessMessages.MOVIE_SHOWTIMES_FETCHED_SUCCESSFULLY);
    }

    @Operation(hidden = true)
    @GetMapping("/{id}/show-times")
    public ResponseEntity<ResponseMessage<List<ShowtimeResponse>>> getMovieShowtimesLegacy(@PathVariable Long id) {
        return getMovieShowtimes(id);
    }

    private Pageable pageable(int page, int size, String sort, String type) {
        if (page < 0) {
            throw new BadRequestException("Page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            throw new BadRequestException("Unsupported movie sort field: " + sort);
        }
        if (!type.equalsIgnoreCase("asc") && !type.equalsIgnoreCase("desc")) {
            throw new BadRequestException("Sort type must be asc or desc");
        }
        Sort.Direction direction = type.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sort));
    }

    private ResponseEntity<ResponseMessage<PageResponse<MovieResponse>>> pageResponse(
            Page<MovieResponse> movies) {
        return ok(PageResponse.from(movies), SuccessMessages.MOVIES_FETCHED_SUCCESSFULLY);
    }

    private <T> ResponseEntity<ResponseMessage<T>> ok(T object, String message) {
        return ResponseEntity.ok(response(object, message, HttpStatus.OK));
    }

    private <T> ResponseMessage<T> response(T object, String message, HttpStatus status) {
        return ResponseMessage.<T>builder()
                .object(object)
                .message(message)
                .httpStatus(status)
                .build();
    }
}
