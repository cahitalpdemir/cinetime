package com.tpe.cinetime.payload.request.user;

import com.tpe.cinetime.constants.messages.ValidationMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordRequestDTO {

    @NotBlank(message = ValidationMessages.CURRENT_PASSWORD_NOT_BLANK)
    private String currentPassword;

    @NotBlank(message = ValidationMessages.NEW_PASSWORD_NOT_BLANK)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&_#]{8,}$",
            message = ValidationMessages.PASSWORD_PATTERN
    )
    private String newPassword;

    @NotBlank(message = ValidationMessages.CONFIRM_PASSWORD_NOT_BLANK)
    private String confirmPassword;
}
