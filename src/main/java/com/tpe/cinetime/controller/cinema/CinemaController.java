package com.tpe.cinetime.controller.cinema;

import com.tpe.cinetime.payload.response.cinema.CinemaResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.cinema.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    // GET /cinemas
    @GetMapping
    public ResponseEntity<ResponseMessage<List<CinemaResponseDTO>>> getAllCinemas() {
        return ResponseEntity.ok(cinemaService.getAllCinemas());
    }

    // GET /cinemas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<CinemaResponseDTO>> getCinemaById(@PathVariable Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }
}
