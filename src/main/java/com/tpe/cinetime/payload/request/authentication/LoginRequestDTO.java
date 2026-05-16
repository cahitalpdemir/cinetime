package com.tpe.cinetime.payload.request.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be in a valid format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
