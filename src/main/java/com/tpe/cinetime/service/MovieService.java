package com.tpe.cinetime.service;


import com.tpe.cinetime.entity.Movie;
import com.tpe.cinetime.enums.MovieStatus;
import com.tpe.cinetime.exception.NotFoundException;
import com.tpe.cinetime.payload.request.MovieRequest;
import com.tpe.cinetime.payload.response.MovieResponse;
import com.tpe.cinetime.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public Page<MovieResponse> getAllMovies(String query, Pageable pageable) {
        Page<Movie> movies;
        if (query != null && !query.isBlank()) {
            movies = movieRepository.search(query, pageable);
        } else {
            movies = movieRepository.findAll(pageable);
        }
        return movies.map(this::mapToResponse);
    }

    public MovieResponse getMovieBySlug(String slug) {
        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Movie not found with slug: " + slug));
        return mapToResponse(movie);
    }

    public Page<MovieResponse> getMoviesBySpecialHall(String hall, Pageable pageable) {
        return movieRepository.findBySpecialHall(hall, pageable).map(this::mapToResponse);
    }

    public Page<MovieResponse> getMoviesInTheaters(Pageable pageable) {
        return movieRepository.findByStatus(MovieStatus.NOW_SHOWING, pageable).map(this::mapToResponse);
    }

    public Page<MovieResponse> getMoviesComingSoon(Pageable pageable) {
        return movieRepository.findByStatus(MovieStatus.COMING_SOON, pageable).map(this::mapToResponse);
    }

    public Page<MovieResponse> getMoviesArchived(Pageable pageable) {
        return movieRepository.findByStatus(MovieStatus.ARCHIVED, pageable).map(this::mapToResponse);
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found with id: " + id));
        return mapToResponse(movie);
    }

    public MovieResponse getMovieByIdAdmin(Long id) {
        return getMovieById(id);
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .slug(generateSlug(request.getTitle()))
                .summary(request.getSummary())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .rating(request.getRating())
                .director(request.getDirector())
                .cast(request.getCast())
                .formats(request.getFormats())
                .genre(request.getGenre())
                .posterUrl(normalizeOptionalString(request.getPosterUrl()))
                .trailerUrl(normalizeOptionalString(request.getTrailerUrl()))
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON)
                .specialHalls(request.getSpecialHalls())
                .build();

        return mapToResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found with id: " + id));

        movie.setTitle(request.getTitle());
        movie.setSlug(generateSlug(request.getTitle()));
        movie.setSummary(request.getSummary());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setDuration(request.getDuration());
        movie.setRating(request.getRating());
        movie.setDirector(request.getDirector());
        movie.setCast(request.getCast());
        movie.setFormats(request.getFormats());
        movie.setGenre(request.getGenre());
        movie.setPosterUrl(normalizeOptionalString(request.getPosterUrl()));
        movie.setTrailerUrl(normalizeOptionalString(request.getTrailerUrl()));
        movie.setStatus(request.getStatus() != null ? request.getStatus() : movie.getStatus());
        movie.setSpecialHalls(request.getSpecialHalls());

        return mapToResponse(movieRepository.save(movie));
    }

    @Transactional
    public MovieResponse deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found with id: " + id));
        MovieResponse response = mapToResponse(movie);
        movieRepository.delete(movie);
        return response;
    }

    private String normalizeOptionalString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String generateSlug(String title) {        return title.toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    private MovieResponse mapToResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .slug(movie.getSlug())
                .summary(movie.getSummary())
                .releaseDate(movie.getReleaseDate())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .director(movie.getDirector())
                .cast(movie.getCast() != null ? new ArrayList<>(movie.getCast()) : null)
                .formats(movie.getFormats() != null ? new ArrayList<>(movie.getFormats()) : null)
                .genre(movie.getGenre())
//                .posterId(movie.getPoster() != null ? movie.getPoster().getId() : null)
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .status(movie.getStatus())
                .specialHalls(movie.getSpecialHalls())
                .createdAt(movie.getCreatedAt())
                .updatedAt(movie.getUpdatedAt())
                .build();
    }
}
