package com.tpe.cinetime.controller;

import com.tpe.cinetime.payload.request.RegisterRequestDTO;
import com.tpe.cinetime.payload.response.ResponseMessage;
import com.tpe.cinetime.payload.response.UserResponseDTO;
import com.tpe.cinetime.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> register(
            @Valid
            @RequestBody
            RegisterRequestDTO registerRequestDTO){


        return ResponseEntity.ok(authService.register(registerRequestDTO));


    }
}
