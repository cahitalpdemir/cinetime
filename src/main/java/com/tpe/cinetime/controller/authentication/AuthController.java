package com.tpe.cinetime.controller.authentication;

import com.tpe.cinetime.payload.request.authentication.*;
import com.tpe.cinetime.payload.response.authentication.LoginResponseDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.payload.response.authentication.RefreshTokenResponseDTO;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.service.authentication.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseMessage<RefreshTokenResponseDTO>> refreshToken(
            @Valid
            @RequestBody
            RefreshTokenRequestDTO refreshTokenRequestDTO){

        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequestDTO));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage<Void>> logout(Authentication authentication){

        return ResponseEntity.ok(authService.logout(authentication));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseMessage<String>> forgotPassword(
            @Valid
            @RequestBody
            ForgotPasswordRequestDTO forgotPasswordRequestDTO){

        return ResponseEntity.ok(authService.forgotPassword(forgotPasswordRequestDTO));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseMessage<String>> resetPassword(
            @Valid
            @RequestBody
            ResetPasswordRequestDTO resetPasswordRequestDTO){

        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequestDTO));
    }


}
