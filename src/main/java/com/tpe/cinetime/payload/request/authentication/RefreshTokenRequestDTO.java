package com.tpe.cinetime.payload.request.authentication;

import com.tpe.cinetime.constants.messages.ValidationMessages;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RefreshTokenRequestDTO {

    @NotBlank(message = ValidationMessages.REFRESH_TOKEN_NOT_BLANK)
    private String refreshToken;
}
