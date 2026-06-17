package com.tpe.cinetime.controller.showtime;

import com.tpe.cinetime.payload.request.showtime.ShowtimeRequest;
import com.tpe.cinetime.payload.response.showtime.SeatAvailabilityResponse;
import com.tpe.cinetime.payload.response.showtime.ShowtimeResponse;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.showtime.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // admin creates a new showtime
    @PostMapping("/admin/showtimes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage<ShowtimeResponse>> createShowtime(
            @Valid @RequestBody ShowtimeRequest request) {
        return ResponseEntity.status(201).body(showtimeService.createShowtime(request));
    }

    // admin cancels a showtime
    @PatchMapping("/admin/showtimes/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseMessage<ShowtimeResponse>> cancelShowtime(
            @PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.cancelShowtime(id));
    }

    //public — filter showtimes by optional movieId, hallId and date
    @GetMapping("/showtimes")
    public ResponseEntity<ResponseMessage<List<ShowtimeResponse>>> getShowtimes(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long hallId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(showtimeService.getShowtimes(movieId, hallId, date));
    }

    //public — returns all seats with booked or available status
    @GetMapping("/showtimes/{id}/seats")
    public ResponseEntity<ResponseMessage<List<SeatAvailabilityResponse>>> getShowtimeSeats(
            @PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getShowtimeSeats(id));
    }
}
