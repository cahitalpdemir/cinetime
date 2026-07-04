package com.tpe.cinetime.controller;



import com.tpe.cinetime.payload.request.MovieRequest;
import com.tpe.cinetime.payload.response.MovieResponse;
import com.tpe.cinetime.payload.response.showtime.ShowtimeResponse;
import com.tpe.cinetime.service.MovieService;
import com.tpe.cinetime.service.showtime.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowtimeService showtimeService;

    // M01 & M08 - Get all movies with search
    @GetMapping
    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        Pageable pageable = PageRequest.of(page, size,
                type.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(movieService.getAllMovies(q, pageable));
    }

    // M02 - Get movie by slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<MovieResponse> getMovieBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(movieService.getMovieBySlug(slug));
    }

    // M03 - Get movies by special hall
    @GetMapping("/hall/{hall}")
    public ResponseEntity<Page<MovieResponse>> getMoviesByHall(
            @PathVariable String hall,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        Pageable pageable = PageRequest.of(page, size,
                type.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(movieService.getMoviesBySpecialHall(hall, pageable));
    }

    // M04 - Get movies in theaters
    @GetMapping("/in-theaters")
    public ResponseEntity<Page<MovieResponse>> getMoviesInTheaters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        Pageable pageable = PageRequest.of(page, size,
                type.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(movieService.getMoviesInTheaters(pageable));
    }

    // M05 - Get movies coming soon
    @GetMapping("/coming-soon")
    public ResponseEntity<Page<MovieResponse>> getMoviesComingSoon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        Pageable pageable = PageRequest.of(page, size,
                type.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(movieService.getMoviesComingSoon(pageable));
    }

    // M06 - Get archived movies
    @GetMapping("/archived")
    public ResponseEntity<Page<MovieResponse>> getMoviesArchived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String type) {
        Pageable pageable = PageRequest.of(page, size,
                type.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(movieService.getMoviesArchived(pageable));
    }

    // M09 - Get movie by id
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    // M10 - Get movie by id (admin)
    @GetMapping("/{id}/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MovieResponse> getMovieByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieByIdAdmin(id));
    }

    // M11 - Create movie
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.createMovie(request));
    }

    // M12 - Update movie
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable Long id,
                                                      @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    // M13 - Delete movie
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MovieResponse> deleteMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.deleteMovie(id));
    }

    // M14 - Get showtimes for movie
    @GetMapping("/{id}/show-times")
    public ResponseEntity<List<ShowtimeResponse>> getMovieShowtimes(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getUpcomingShowtimesByMovieId(id));
    }
}
