package com.tpe.cinetime.controller.admin;

import com.tpe.cinetime.payload.request.cinema.CinemaRequestDTO;
import com.tpe.cinetime.payload.request.cinema.HallRequestDTO;
import com.tpe.cinetime.payload.response.cinema.CinemaResponseDTO;
import com.tpe.cinetime.payload.response.cinema.HallResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.service.cinema.CinemaService;
import com.tpe.cinetime.service.cinema.HallService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminCinemaController {

    private final CinemaService cinemaService;
    private final HallService hallService;

    // POST /admin/cinemas
    @PostMapping("/cinemas")
    public ResponseEntity<ResponseMessage<CinemaResponseDTO>> saveCinema(
            @Valid @RequestBody CinemaRequestDTO cinemaRequestDTO) {
        ResponseMessage<CinemaResponseDTO> response = cinemaService.saveCinema(cinemaRequestDTO);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    // POST /admin/halls
    @PostMapping("/halls")
    public ResponseEntity<ResponseMessage<HallResponseDTO>> saveHall(
            @Valid @RequestBody HallRequestDTO hallRequestDTO) {
        ResponseMessage<HallResponseDTO> response = hallService.saveHall(hallRequestDTO);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
