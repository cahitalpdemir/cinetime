package com.tpe.cinetime.payload.request.authentication;

import com.tpe.cinetime.constants.messages.ValidationMessages;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = ValidationMessages.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessages.EMAIL_NOT_VALID)
    private String email;

    @NotBlank(message = ValidationMessages.PASSWORD_NOT_BLANK)
    private String password;
}
