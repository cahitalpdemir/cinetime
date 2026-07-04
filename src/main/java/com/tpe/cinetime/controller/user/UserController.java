package com.tpe.cinetime.controller.user;


import com.tpe.cinetime.payload.request.user.UserUpdatePasswordRequestDTO;
import com.tpe.cinetime.payload.request.user.UserUpdateWithoutPasswordRequestDTO;
import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import com.tpe.cinetime.payload.response.user.UserResponseDTO;
import com.tpe.cinetime.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> getAccountDetails() {

        return ResponseEntity.ok(userService.getAccountDetails());
    }

    @PatchMapping("/me")
    public ResponseEntity<ResponseMessage<UserResponseDTO>> updateAccountDetails(
            @Valid
            @RequestBody
            UserUpdateWithoutPasswordRequestDTO userUpdateWithPasswordRequestDTO) {

        return ResponseEntity.ok(userService.updateAccountDetails(userUpdateWithPasswordRequestDTO));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ResponseMessage<?>> updatePassword(
            @Valid
            @RequestBody
            UserUpdatePasswordRequestDTO userUpdatePasswordRequestDTO) {

        return ResponseEntity.ok(userService.updatePassword(userUpdatePasswordRequestDTO));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ResponseMessage<?>> deleteAccount() {

        return ResponseEntity.ok(userService.deleteAccount());
    }

}
