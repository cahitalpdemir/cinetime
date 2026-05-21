package com.tpe.cinetime.payload.request.authentication;

import com.tpe.cinetime.constants.messages.ValidationMessages;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ResetPasswordRequestDTO {

    @NotBlank(message = ValidationMessages.RESET_PASSWORD_TOKEN_NOT_BLANK)
    private String resetPasswordToken;

    @NotBlank(message = ValidationMessages.NEW_PASSWORD_NOT_BLANK)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&_#]{8,}$",
            message = ValidationMessages.PASSWORD_PATTERN
    )
    private String newPassword;

    @NotBlank(message = ValidationMessages.CONFIRM_PASSWORD_NOT_BLANK)
    private String confirmPassword;
}
