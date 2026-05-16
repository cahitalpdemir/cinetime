package com.tpe.cinetime.controller.authentication;

import com.tpe.cinetime.payload.request.authentication.LoginRequestDTO;
import com.tpe.cinetime.payload.request.authentication.RegisterRequestDTO;
import com.tpe.cinetime.payload.response.authentication.LoginResponseDTO;
import com.tpe.cinetime.payload.response.ResponseMessage;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
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

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage<LoginResponseDTO>> login(
            @Valid
            @RequestBody
            LoginRequestDTO loginRequestDTO){

        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }


}
