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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/cinemas")
    public ResponseEntity<ResponseMessage<CinemaResponseDTO>> saveCinema(
            @Valid @RequestBody CinemaRequestDTO cinemaRequestDTO) {
        ResponseMessage<CinemaResponseDTO> response = cinemaService.saveCinema(cinemaRequestDTO);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PutMapping("/cinemas/{id}")
    public ResponseEntity<ResponseMessage<CinemaResponseDTO>> updateCinema(
            @PathVariable Long id,
            @Valid @RequestBody CinemaRequestDTO cinemaRequestDTO) {
        ResponseMessage<CinemaResponseDTO> response = cinemaService.updateCinema(id, cinemaRequestDTO);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/cinemas/{id}")
    public ResponseEntity<ResponseMessage<CinemaResponseDTO>> deleteCinema(@PathVariable Long id) {
        ResponseMessage<CinemaResponseDTO> response = cinemaService.deleteCinema(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping("/halls")
    public ResponseEntity<ResponseMessage<HallResponseDTO>> saveHall(
            @Valid @RequestBody HallRequestDTO hallRequestDTO) {
        ResponseMessage<HallResponseDTO> response = hallService.saveHall(hallRequestDTO);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PutMapping("/halls/{id}")
    public ResponseEntity<ResponseMessage<HallResponseDTO>> updateHall(
            @PathVariable Long id,
            @Valid @RequestBody HallRequestDTO hallRequestDTO) {
        ResponseMessage<HallResponseDTO> response = hallService.updateHall(id, hallRequestDTO);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/halls/{id}")
    public ResponseEntity<ResponseMessage<HallResponseDTO>> deleteHall(@PathVariable Long id) {
        ResponseMessage<HallResponseDTO> response = hallService.deleteHall(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}